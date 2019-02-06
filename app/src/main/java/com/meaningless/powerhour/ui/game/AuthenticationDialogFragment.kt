package com.meaningless.powerhour.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.meaningless.powerhour.R
import kotlinx.android.synthetic.main.dialog_authentication.view.*

class AuthenticationDialogFragment : DialogFragment() {

    var delegate: Delegate? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_authentication, container, false)
        view.authenticationCancelButton.setOnClickListener {
            dismiss()
        }
        view.authenticationContinueButton.setOnClickListener {
            delegate?.onContinueAuthentication()
            dismiss()
        }
        return view
    }

    interface Delegate {
        fun onContinueAuthentication()
    }
}