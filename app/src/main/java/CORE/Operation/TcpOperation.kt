package vivas.com.myrouter.Operation

import com.google.protobuf.ExtensionRegistryLite
import comm.CommModel
import vivas.com.myrouter.Communication.TcpComponent
import vivas.com.myrouter.Component.ComponentType
import vivas.com.myrouter.Engine
import vivas.com.myrouter.Message.CoreMessage


/**
 * Created by dung.nt on 6/27/17.
 */
abstract class TcpOperation() : BaseOperation() {

    var replyData: ByteArray? = null
    var ex: Exception? = null

    abstract fun buildRequest(): ByteArray?
    abstract fun onReplyRequest(type : Int,reply : CommModel.Reply)
    abstract fun apiId(): Int
    abstract fun extension() : ExtensionRegistryLite?
    override protected fun fire() {


        if (replyData == null) {
            var data = this.buildRequest()
            if (data != null) {
                val coreMessage = CoreMessage(this.apiId(), 1, data)
                var tcp = Engine.instance.getComponent(ComponentType.TCP)
                if (tcp is TcpComponent) {
                    tcp.sendMessage(this, coreMessage)
                }
            } else {

            }
        } else {
            // REPLY
            try {
                var ex = extension()
                var rep : CommModel.Reply? = null

                if (ex == null){
                    rep = CommModel.Reply.parseFrom(this.replyData!!)

                }else{
                    rep = CommModel.Reply.parseFrom(this.replyData!!,extension())

                }

                if (rep != null){
                    if (rep?.presencesCount != 0){
                        /// SU LY KHI CO NGUOI DUNG TRONG CONTACT ONLINE - OFFLINE - HAY REG VIET TALK
                    }
                    this.onReplyRequest(rep.type,rep)
                }




            }catch (ex : Exception){
                println(ex.toString())
            }

        }
    }
}
