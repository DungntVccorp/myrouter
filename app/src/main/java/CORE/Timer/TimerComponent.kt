package vivas.com.myrouter.Timer

import vivas.com.myrouter.Component.BaseComponent
import vivas.com.myrouter.Component.ComponentType
import vivas.com.myrouter.Operation.BaseOperation
import java.util.*
import kotlin.concurrent.timerTask

/**
 * Created by dung.nt on 7/6/17.
 */
class TimerComponent : BaseComponent{


    var timer  = Timer("timmer")


    override fun priority(): Int {
        return 1
    }

    override fun loadConfig() {

    }

    override fun start() {




    }

    override fun reset() {
    }

    override fun componentType(): ComponentType {
        return ComponentType.TIMMER
    }



    fun addTask(op : BaseOperation,isRepeat : Boolean = false,period : Long){ ///  period tính theo giây / 1000
        if (isRepeat){
            timer.scheduleAtFixedRate(timerTask { op.enqueue() },0,period)
        }else{
            timer.schedule(timerTask { op.enqueue() },period)
        }
    }



}