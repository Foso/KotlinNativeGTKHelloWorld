package sample.gtk

import gtk3.*
import kotlinx.cinterop.CFunction
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.staticCFunction

// Note that all callback parameters must be primitive types or nullable C pointers.
fun <F : CFunction<*>> g_signal_connect(
    obj: CPointer<*>, actionName: String,
    action: CPointer<F>, data: gpointer? = null, connect_flags: GConnectFlags = 0u
) {
    g_signal_connect_data(
        obj.reinterpret(), actionName, action.reinterpret(),
        data = data, destroy_data = null, connect_flags = connect_flags
    )

}


interface Contract{
    interface View{
        fun closeView(widget: CPointer<GtkWidget>?)
    }
    interface Presenter{
        fun onCreate()
        fun onButtonClicked(widget: CPointer<GtkWidget>?)
    }
}

class MyPresenter(val view: Contract.View):Contract.Presenter{
    override fun onCreate() {

    }

    override fun onButtonClicked(widget: CPointer<GtkWidget>?) {
        view.closeView(widget)

    }

}


class FirstWindow(val app: CPointer<GtkApplication>?, val user_data: gpointer?) : Contract.View {

    val presenter : Contract.Presenter = MyPresenter(this)

    fun activate() {
        val windowWidget = gtk_application_window_new(app)!!
        val window = windowWidget.reinterpret<GtkWindow>()
        gtk_window_set_title(window, "Window")
        gtk_window_set_default_size(window, 200, 200)

        val button_box = gtk_button_box_new(
            GtkOrientation.GTK_ORIENTATION_HORIZONTAL
        )!!
        gtk_container_add(window.reinterpret(), button_box)

        val button = gtk_button_new_with_label("Hello World: clic  k me!")!!


        g_signal_connect(
            button, "clicked",
            staticCFunction { widget: CPointer<GtkWidget>? ->
                gtk_widget_destroy(widget)
               // presenter.onButtonClicked(widget)
            },
            window, G_CONNECT_SWAPPED
        )
        gtk_container_add(button_box.reinterpret(), button)


        gtk_widget_show_all(windowWidget)
        presenter.onCreate()
    }



    override fun closeView(widget: CPointer<GtkWidget>?) {
        gtk_widget_destroy(widget)
    }
}