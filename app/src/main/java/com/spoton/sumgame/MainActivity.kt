package com.spoton.sumgame

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {

    private val equation: TextView by lazy { findViewById(R.id.game_equation) }
    private val result: TextInputEditText by lazy { findViewById(R.id.game_result) }
    private val check: Button by lazy { findViewById(R.id.game_check) }
    private val possibleAnchors: IntArray = intArrayOf(
        R.id.anchor_1,
        R.id.anchor_2,
        R.id.anchor_3,
        R.id.anchor_4
    )

    private val vm = MainViewModel(possibleAnchors)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        vm.viewState.observe(this, ::onViewState)

        check.setOnClickListener {
            vm.onCheckClicked(result.text.toString())
            val manager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            manager.hideSoftInputFromWindow(result.windowToken, 0)
        }
    }

    private fun onViewState(state: MainViewModel.ViewState) {
        when (state) {
            is MainViewModel.ViewState.Display -> onViewStateDisplay(state)
            is MainViewModel.ViewState.Warning -> onViewStateWarning(state)
            MainViewModel.ViewState.GameOver -> onViewStateGameOver()
        }
    }

    private fun onViewStateDisplay(state: MainViewModel.ViewState.Display) {
        equation.text = state.equation
        result.setText("")
        val anchor = findViewById<View>(state.anchorId)
        anchor.doOnPreDraw {
            check.x = anchor.x
            check.y = anchor.y
        }
        if (state.shouldShowSnackbar) {
            Snackbar.make(findViewById(R.id.container), state.snackbarText, Snackbar.LENGTH_SHORT)
                .show()
        }
    }

    private fun onViewStateWarning(state: MainViewModel.ViewState.Warning) {
        Snackbar.make(findViewById(R.id.container), state.text, Snackbar.LENGTH_SHORT).show()
    }

    private fun onViewStateGameOver() {
        Snackbar.make(findViewById(R.id.container), "Game Over!", Snackbar.LENGTH_SHORT).show()
        check.isVisible = false
    }
}