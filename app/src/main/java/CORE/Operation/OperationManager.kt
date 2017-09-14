package vivas.com.myrouter.Operation

import android.os.Build
import vivas.com.myrouter.Component.BaseComponent
import vivas.com.myrouter.Component.ComponentType
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Created by dung.nt on 6/27/17.
 */
class OperationManager : BaseComponent {

    private var executorService : ExecutorService? = null


    override fun priority(): Int {
        return 1
    }

    override fun loadConfig() {
        if (Build.VERSION.SDK_INT > 17){
            this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 3)
        }else{
            this.executorService = Executors.newFixedThreadPool(5)
        }

    }

    override fun start() {

    }

    override fun reset() {
        this.executorService?.shutdown()

    }

    override fun componentType(): ComponentType {
        return ComponentType.OPERATION
    }



    fun enqueue(op : BaseOperation){
        this.executorService?.execute(op)
    }





}