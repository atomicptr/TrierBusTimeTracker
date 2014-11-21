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
import de.kasoki.trierbustimetracker.utils.FavoritesManager
import de.kasoki.trierbustimetracker.utils.ShortcutManager

class BusTimeActivity extends SActivity {

    val timesAdapter:BusTimeAdapter = new BusTimeAdapter(this)
    var code:String = ""

    override def onCreate(bundle:Bundle) {
        super.onCreate(bundle)

        invalidateOptionsMenu()

        this.setContentView(R.layout.activity_bustimes)

        val intent = getIntent()

        ActionBarHelper.colorActionBar(this)

        if(intent.hasExtra("FROM_MAIN_ACTIVITY")) {
            ActionBarHelper.enableHomeAsUp(this)
        }

        handleIntent(intent)
    }

    private def handleIntent(intent:Intent) {
        this.code = intent.getStringExtra("BUS_TIME_CODE")

        val listView = list()

        listView.setAdapter(timesAdapter)

        val busStop = BusStop.getBusStopByCode(this.code)

        // set title to bus stop
        this.setTitle(busStop.name)

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

        val favoritesItem = menu.findItem(R.id.action_favorite)

        val busStop = BusStop.getBusStopByCode(this.code)

        if(FavoritesManager.has(this, busStop)) {
            favoritesItem.setIcon(R.drawable.ic_favorite_filled)
        }

        return true;
    }

    override def onOptionsItemSelected(item:MenuItem):Boolean = {
        item.getItemId() match {
            case android.R.id.home => {
                finish()

                AndroidHelper.slideBack(this)

                return true
            }

            case R.id.action_favorite => {
                val busStop = BusStop.getBusStopByCode(this.code)

                if(FavoritesManager.has(this, busStop)) {
                    item.setIcon(R.drawable.ic_favorite_empty)

                    FavoritesManager.remove(this, busStop)

                    val message = getResources().getString(R.string.favorite_item_deleted, busStop.name)
                    toast(message)
                } else {
                    item.setIcon(R.drawable.ic_favorite_filled)

                    FavoritesManager.add(this, busStop)

                    val message = getResources().getString(R.string.favorite_item_added, busStop.name)
                    toast(message)
                }

                invalidateOptionsMenu()

                return true
            }

            case R.id.action_reload => {
                reload()

                return true
            }

            case R.id.action_add_to_homescreen => {
                val busStop = BusStop.getBusStopByCode(code)

                ShortcutManager.create(this, busStop)

                toast(getString(R.string.shortcut_created, busStop.name))

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
                val timeout = 5000 // 5s
                BusTime.timeout = timeout
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

        if(getIntent.hasExtra("FROM_MAIN_ACTIVITY")) {
            AndroidHelper.slideBack(BusTimeActivity.this)
        }
    }

    override def onBackPressed() {
        super.onBackPressed()

        AndroidHelper.slideBack(this)
    }

    def list():ListView = this.findViewById(R.id.busStopListView).asInstanceOf[ListView]
}
