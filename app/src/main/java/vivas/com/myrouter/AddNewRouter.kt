package vivas.com.myrouter

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import okhttp3.*
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.io.IOException
import java.util.concurrent.TimeUnit

class AddNewRouter : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_router)

        title = "Thêm Router"

        find<Button>(R.id.btn_Add).onClick {

            val name = find<TextView>(R.id.txtName).text.toString()
            val ip = find<TextView>(R.id.txt_ip).text.toString()
            val username = find<TextView>(R.id.txt_username).text.toString()
            val password = find<TextView>(R.id.txt_password).text.toString()
            val port = find<TextView>(R.id.txt_port).text.toString()
            val des = find<TextView>(R.id.txt_description).text.toString()

            if (name.isEmpty() || ip.isEmpty() || username.isEmpty() || password.isEmpty() || port.isEmpty() || des.isEmpty()){

            }else{
                /// SUBMIT


                val client = OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS).readTimeout(5, TimeUnit.SECONDS).writeTimeout(5, TimeUnit.SECONDS).authenticator(object : Authenticator {
                    override fun authenticate(route: Route?, response: Response?): Request? {

                        return response?.request()?.newBuilder()?.header("Authorization", Credentials.basic("dungnt","12345"))?.build()
                    }
                }).build()


                val multipartBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("name", name)
                        .addFormDataPart("ip_address", ip)
                        .addFormDataPart("username", username)
                        .addFormDataPart("password", password)
                        .addFormDataPart("description", des)
                        .addFormDataPart("port",port)
                        .build()




                val request = Request.Builder().url("http://192.168.70.247:8080/api/router").post(multipartBody).build()

                val newCall = client.newCall(request)


                newCall.enqueue(object  : Callback{
                    override fun onFailure(call: Call?, e: IOException?) {
                        println(e.toString())
                    }

                    override fun onResponse(call: Call?, response: Response?) {
                        println(response?.body()?.string())
                    }
                })




            }



        }

    }




}
