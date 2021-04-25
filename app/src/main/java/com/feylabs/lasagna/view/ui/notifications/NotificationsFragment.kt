package com.feylabs.lasagna.view.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.feylabs.lasagna.R
import com.feylabs.lasagna.view.MainMenuUserActivity
import com.feylabs.lasagna.view.MainMenuUserViewModel

class NotificationsFragment : Fragment() {

  private lateinit var notificationsViewModel: NotificationsViewModel
  val menuViewModel by lazy { ViewModelProvider(requireActivity()).get(MainMenuUserViewModel::class.java) }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)
    val root = inflater.inflate(R.layout.fragment_notifications, container, false)
    val textView: TextView = root.findViewById(R.id.text_notifications)
    notificationsViewModel.text.observe(viewLifecycleOwner, Observer {
      textView.text = it
    })
    return root
  }


  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    menuViewModel.title.value="Notification"
  }
}