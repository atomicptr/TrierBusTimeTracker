package de.kasoki.trierbustimetracker

import org.scaloid.common._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success
import scala.util.Failure
import scala.collection.mutable.Buffer

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget._
import android.util.Log
import android.content._

import de.kasoki.swtrealtime._

import de.kasoki.trierbustimetracker.adapter.BusTimeAdapter
import de.kasoki.trierbustimetracker.utils.ActionBarHelper
import de.kasoki.trierbustimetracker.utils.AndroidHelper

class BusTimeActivity extends SActivity {

    val timesAdapter:BusTimeAdapter = new BusTimeAdapter(this)
    var code:String = ""

    override def onCreate(bundle:Bundle) {
        super.onCreate(bundle)

        this.setContentView(R.layout.activity_bustimes)

        ActionBarHelper.colorActionBar(this)
        ActionBarHelper.enableHomeAsUp(this)

        val intent = getIntent()

        handleIntent(intent)
    }

    private def handleIntent(intent:Intent) {
        this.code = intent.getStringExtra("BUS_TIME_CODE")

        val listView = list()

        listView.setAdapter(timesAdapter)

        // set title to bus stop
        this.setTitle(BusStop.getBusStopByCode(this.code).name)

        // get information
        reload()
    }

    override def onNewIntent(intent:Intent) {
        super.onNewIntent(intent)

        setIntent(intent)
        handleIntent(intent)
    }

    override def onCreateOptionsMenu(menu:Menu):Boolean = {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.bustimes_menu, menu)
        return true;
    }

    override def onOptionsItemSelected(item:MenuItem):Boolean = {
        item.getItemId() match {
            case android.R.id.home => {
                finish()

                AndroidHelper.slideBack(this)

                return true
            }

            case R.id.action_reload => {
                reload()

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    def reload() {
        if(AndroidHelper.isNetworkAvailable(this)) {

            val loadingText = getResources().getString(R.string.loading_dialog_text)

            val progressDialog = spinnerDialog("", loadingText)

            val retryText = getResources().getString(R.string.retry_text)
            val cancelText = getResources().getString(android.R.string.cancel)

            val future = Future {
                BusTime.timeout = 5000 // 5s
                BusTime.fromStopCode(this.code)
            }

            future onComplete {
                case Success(times) => {

                    runOnUiThread({
                        progressDialog.dismiss()

                        if(times.length > 0) {
                            this.timesAdapter.items = times.sorted

                            this.timesAdapter.notifyDataSetChanged()
                        } else {
                            val message = getResources().getString(R.string.bustime_no_bus)

                            new AlertDialogBuilder("", message) {
                                positiveButton(retryText, reload())
                                setNegativeButton(cancelText, new DialogInterface.OnClickListener {
                                    def onClick(dialog:DialogInterface, which:Int):Unit = closeActivity()
                                })
                                setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    def onCancel(dialog:DialogInterface):Unit = closeActivity()
                                })
                            }.show()
                        }
                    })
                }

                case Failure(t:Throwable) => {
                    progressDialog.dismiss()

                    error(t.toString)

                    val sw = new java.io.StringWriter()
                    t.printStackTrace(new java.io.PrintWriter(sw))
                    val str = sw.toString()

                    error(str)

                    new AlertDialogBuilder("", t.toString) {
                        positiveButton(retryText, reload())
                        setNegativeButton(cancelText, new DialogInterface.OnClickListener {
                            def onClick(dialog:DialogInterface, which:Int):Unit = {
                                if(timesAdapter.items.length > 0) {
                                    dialog.dismiss()
                                } else {
                                    closeActivity()
                                }
                            }
                        })
                        setOnCancelListener(new DialogInterface.OnCancelListener() {
                            def onCancel(dialog:DialogInterface):Unit = {
                                if(timesAdapter.items.length > 0) {
                                    dialog.dismiss()
                                } else {
                                    closeActivity()
                                }
                            }
                        })
                    }.show()
                }
            }

        } else {
            // No connection
            debug("NO NETWORK CONNECTION")

            val noNetwork = getResources().getString(R.string.no_network_connection_text)

            val retryText = getResources().getString(R.string.retry_text)
            val cancelText = getResources().getString(android.R.string.cancel)

            new AlertDialogBuilder("", noNetwork) {
                positiveButton(retryText, reload())
                setNegativeButton(cancelText, new DialogInterface.OnClickListener {
                    def onClick(dialog:DialogInterface, which:Int):Unit = {
                        if(timesAdapter.items.length > 0) {
                            dialog.dismiss()
                        } else {
                            closeActivity()
                        }
                    }
                })
                setOnCancelListener(new DialogInterface.OnCancelListener() {
                    def onCancel(dialog:DialogInterface):Unit = {
                        if(timesAdapter.items.length > 0) {
                            dialog.dismiss()
                        } else {
                            closeActivity()
                        }
                    }
                })
            }.show()
        }
    }

    def closeActivity() {
        finish()
        AndroidHelper.slideBack(BusTimeActivity.this)
    }

    override def onBackPressed() {
        super.onBackPressed()

        AndroidHelper.slideBack(this)
    }

    def list():ListView = this.findViewById(R.id.busStopListView).asInstanceOf[ListView]
}