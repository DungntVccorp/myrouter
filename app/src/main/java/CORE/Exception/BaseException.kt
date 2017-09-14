package vivas.com.myrouter.Exception

import vivas.com.myrouter.HTTP_ERROR
import vivas.com.myrouter.TCP_ERROR

/**
 * Created by dung.nt on 6/28/17.
 */
open class BaseException : Exception{

    constructor(message: String?) : super(message) {
    }
}

class HttpException : BaseException{
    var er_code : HTTP_ERROR = HTTP_ERROR.NONE
    constructor(message: String?, er_code: HTTP_ERROR) : super(message) {
        this.er_code = er_code
    }
}
class TcpException : BaseException{
    var er_code : TCP_ERROR = TCP_ERROR.NONE
    constructor(message: String?, er_code: TCP_ERROR) : super(message) {
        this.er_code = er_code
    }
}