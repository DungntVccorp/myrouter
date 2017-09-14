package vivas.com.myrouter.Communication


import vivas.com.myrouter.Component.BaseComponent
import vivas.com.myrouter.Component.ComponentType
import vivas.com.myrouter.Exception.TcpException
import vivas.com.myrouter.Message.CoreMessage
import vivas.com.myrouter.Operation.TcpOperation
import vivas.com.myrouter.loadConfigTcp
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

/**
 * Created by dung.nt on 6/27/17.
 */

class TcpComponent : BaseComponent, TcpConnectionDelegate {

    var hostName: String? = ""
    var hostPort: Int = 0

    var msgID   : Long = 1


    var test : (() -> Unit)? = null
    var onDidConnected : (() -> Unit)? = null
    var onDisconect : ((er : Exception?) -> Unit)? = null

    var client: TcpConnection? = null

    private var listReceiveOperation = java.util.Collections.synchronizedMap(HashMap<Int, String>())
    private var listSendingOperation = java.util.Collections.synchronizedMap(HashMap<Long, HttpComponent.mCall>())

    override fun priority(): Int {
        return 1
    }

    override fun loadConfig() {
        this.loadConfigTcp()
    }

    override fun start() {


    }

    override fun reset() {

    }

    override fun componentType(): ComponentType {
        return ComponentType.TCP
    }

    fun addReceiveOperation(receiveName: String,receiveId : Int) {
        this.listReceiveOperation[receiveId] = receiveName
    }


    fun config(hostName : String,hostPort : Int,onDidConnected : (() -> Unit),onDisconnect : ((er : TcpException?) -> Unit) ) : TcpComponent{
        this.hostName = hostName
        this.hostPort = hostPort
        this.onDidConnected = onDidConnected
        return this
    }

    fun Connect() {
        if (this.hostName == null || this.hostPort == null) {
            println("Config Failure")
        } else {
            client = TcpConnection(this.hostName, this.hostPort, this)
            client?.start()


        }
    }

    fun sendMessage(operation: TcpOperation, msg: CoreMessage) {
        msg.msg_rID = msgID++
        var msgData = msg.encryptMessage()
        if (msgData != null) {
            listSendingOperation[msg.msg_rID] = HttpComponent.mCall(operation.getClassName(),operation.onSuccess,operation.onFailure)
            println("client request operation ${operation.getSortName()} with Id = ${operation.apiId()}")

            this.client?.newCall(msgData!!)
        }
    }

    override fun socketDidDisconect(error: Exception) {
        if (this.onDisconect != null){
            this.onDisconect!!(error)
        }
    }

    override fun socketDidConected() {
        if(onDidConnected != null){
            this.onDidConnected!!()
        }
    }

    override fun socketDidReceiveMessage(msg: CoreMessage) {

        var opName = this.listSendingOperation.remove(msg.msg_rID)

        if (opName != null) {
            val cb = Class.forName(opName.className).newInstance()
            if (cb is TcpOperation) {

                println("server reply operation ${cb.getSortName()} with Id = ${cb.apiId()}")
                cb.replyData = msg.msg_payload
                cb.onSuccess = opName.onSuccess
                cb.onFailure = opName.onFailure
                cb.enqueue()
            }
        }
        else{
            var cName = this.listReceiveOperation.remove(msg.msg_type)
            if (cName != null) {
                val cb = Class.forName(cName).newInstance()
                if (cb is TcpOperation) {
                    println("Client receive operation ${cb.getSortName()} with Id = ${cb.apiId()}")
                    cb.replyData = msg.msg_payload
                    cb.enqueue()
                }
            }
        }

    }


}


interface TcpConnectionDelegate {
    fun socketDidDisconect(error: Exception)
    fun socketDidReceiveMessage(msg: CoreMessage)
    fun socketDidConected()

}


class TcpConnection(val hostName: String?, var hostPort: Int, var delegate: TcpConnectionDelegate?) : Thread() {

    private var socket: Socket? = null

    private var address: InetSocketAddress? = null

    private var inputStream: DataInputStream? = null

    private var outputStream: DataOutputStream? = null

    var isConnected: Boolean = false


    override fun run() {
        super.run()

        try {

            val inetbyName = InetAddress.getByName(this.hostName)

            address = InetSocketAddress(inetbyName, this.hostPort)

            socket = Socket()

            socket?.connect(address)

            inputStream = DataInputStream(socket?.getInputStream())

            outputStream = DataOutputStream(socket?.getOutputStream())

            if (inputStream != null && outputStream != null) {
                isConnected = true
            }

            if (delegate != null) {
                this.delegate?.socketDidConected()
            }
            var buffer: ByteArray? = null

            while (isConnected) {
                if (inputStream!!.available() > 0) {
                    var b: ByteArray = ByteArray(DEFAULT_BUFFER_SIZE)
                    var length: Int = 0

                    length = inputStream!!.read(b)


                    if (buffer == null) {

                        buffer = b.copyOfRange(0, length)
                    } else {


                        buffer = buffer.plus(b.copyOfRange(0, length))

                    }


                    var parse: Boolean = true




                    while (parse) {
                        var decryptMessage = CoreMessage.decryptMessage(buffer!!)

                        if (decryptMessage != null) {
                            if (decryptMessage.msg_size < buffer.size) {
                                /// Bị dư data
                                buffer = buffer!!.copyOfRange(decryptMessage.msg_size, buffer.size)
                            } else {
                                /// done
                                parse = false
                                buffer = null
                            }
                            if (delegate != null) {
                                this.delegate?.socketDidReceiveMessage(decryptMessage!!)
                            }
                        } else {
                            /// bi thieu data
                            parse = false
                        }
                    }

                }
                sleep(10)
            }
        } catch (e: Exception) {
            println(e.toString())
            this.isConnected = false
            this.delegate?.socketDidDisconect(e)
        }

    }


    override fun interrupt() {
        super.interrupt()
        println("Thread Interrupt")
    }


    fun tcpStop() {

    }

    fun tcpReconect() {

    }

    fun newCall(data: ByteArray) {
        this.outputStream?.write(data)
    }

}