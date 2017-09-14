package vivas.com.myrouter

import vivas.com.myrouter.Communication.HttpComponent
import vivas.com.myrouter.Events.Events
import vivas.com.myrouter.Operation.OperationManager
import vivas.com.myrouter.Timer.TimerComponent


/**
 * Created by dung.nt on 6/27/17.
 */

enum class HTTP_ERROR{
    NONE,
    CONFIG_FAILURE,
    LOST_CONNECTION,
    REQUEST_BUILD_FAILURE,
}
enum class TCP_ERROR{
    NONE,
}


fun Engine.loadConfigComponent(){

    this.addComponent(OperationManager())
    this.addComponent(HttpComponent())
    this.addComponent(Events())
    this.addComponent(TimerComponent())


}

fun HttpComponent.loadConfigHttp(){
    this.baseUrl = "https://auth-vt2-beta.wala.vn/vt/r/"
}

fun Events.loadEventConfig(){

}
