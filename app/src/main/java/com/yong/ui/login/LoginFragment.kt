package com.yong.ui.login

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import com.android.volley.VolleyError
import com.yong.R
import com.yong.data.api.ApiHelper
import com.yong.data.model.AuthnResponse
import com.yong.databinding.LoginFragmentBinding
import com.yong.ui.AbsFragment
import com.yong.ui.main.MainFragment
import com.yong.utils.getViewModel


class LoginFragment : AbsFragment() {
    companion object {
        private val TAG = LoginFragment::class.java.simpleName
        fun newInstance(
            fragmentManager: FragmentManager,
            @IdRes id: Int
        ) {
            val mFragment = fragmentManager.findFragmentByTag(TAG) as? LoginFragment
            if (mFragment != null) return
            val fragment = LoginFragment()
            fragmentManager.beginTransaction()
                .replace(id, fragment)
                .commit()
        }
    }

    lateinit var viewDataBinding: LoginFragmentBinding

    private val viewModel by lazy { getViewModel(LoginViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewDataBinding = LoginFragmentBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewmodel = viewModel
        }

        viewDataBinding.signinButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                viewModel.errorText.value = null
                val account = viewDataBinding.accountText.text
                val password = viewDataBinding.passwordText.text

                if (account.isNullOrEmpty() || password.isNullOrEmpty()) {
                    viewModel.errorText.value = "account or password is blank"
                    return
                }
                viewModel.performAuthn(account.toString(), password.toString(), responseListener)
            }
        })

        return viewDataBinding.root
    }

    private val responseListener = object : ApiHelper.ResponseListener<AuthnResponse> {
        override fun onSuccess(response: AuthnResponse) {
            hideKeyboard()
            MainFragment.newInstance(fragmentManager!!, R.id.container)
        }

        override fun onFailure(error: VolleyError) {
            val message = if (error.message.isNullOrEmpty()) {
                "unknown error please try later"
            } else {
                error.message
            }
            viewModel.errorText.value = message
        }
    }

    fun hideKeyboard() {
        val imm = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager ?: return
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity!!.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}