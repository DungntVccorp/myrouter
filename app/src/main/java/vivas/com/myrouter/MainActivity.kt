package vivas.com.myrouter

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import okhttp3.*
import org.jetbrains.anko.find
import org.jetbrains.anko.intentFor
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    lateinit var client : OkHttpClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)






    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.right_add_other_router,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_favorite){
            startActivity(intentFor<AddNewRouter>())
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onStart() {
        super.onStart()


        println("onStart")

        client = OkHttpClient.Builder().connectTimeout(5,TimeUnit.SECONDS).readTimeout(5,TimeUnit.SECONDS).writeTimeout(5,TimeUnit.SECONDS).authenticator(object : Authenticator{
            override fun authenticate(route: Route?, response: Response?): Request? {

                return response?.request()?.newBuilder()?.header("Authorization",Credentials.basic("dungnt","12345"))?.build()
            }
        }).build()

        val request = Request.Builder().url("http://192.168.70.247:8080/api/router").get().build()

        val newCall = client.newCall(request)
        newCall.enqueue(object : Callback{
            override fun onFailure(call: Call?, e: IOException?) {
                println(e.toString())
            }

            override fun onResponse(call: Call?, response: Response?) {
                val jsonObject = JSONObject(response?.body()?.string())
                if (jsonObject.getInt("status") == 200){
                    val jsonArray = jsonObject.getJSONArray("data")
                    val adapter = object : BaseAdapter(){
                        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
                            val v = layoutInflater.inflate(R.layout.routercell,null)
                            v.find<TextView>(R.id.lbl_ip).text = jsonArray.getJSONObject(p0).getString("ip_address")
                            v.find<TextView>(R.id.lbl_usernam).text = jsonArray.getJSONObject(p0).getString("username")
                            v.find<TextView>(R.id.lbl_password).text = jsonArray.getJSONObject(p0).getString("password")
                            v.find<TextView>(R.id.lbl_port).text = jsonArray.getJSONObject(p0).getString("port")
                            v.find<TextView>(R.id.lbldes).text = jsonArray.getJSONObject(p0).getString("description")
                            return v
                        }

                        override fun getItemId(p0: Int): Long {
                            return 0
                        }

                        override fun getItem(p0: Int): Any {
                            return ""
                        }

                        override fun getCount(): Int {
                            return jsonArray.length()
                        }
                    }
                    launch(UI){
                        find<ListView>(R.id.tbl_router).adapter = adapter
                    }


                }else{
                    println(jsonObject.getString("message"))
                }

            }
        })

    }
}
