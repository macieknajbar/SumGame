package com.spoton.sumgame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar
import kotlin.random.Random

class MainViewModel(
    private val anchorIds: IntArray
) : ViewModel() {

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState> = _viewState

    private var firstValue: Int = 0
    private var secondValue: Int = 0
    private var points: Int = 0
    private lateinit var sign: Equation

    private val random = Random(10)

    init {
        nextEquation()
    }

    private fun nextEquation() {
        firstValue = random.nextInt(10)
        secondValue = random.nextInt(10) + 1
        sign = Equation.values()[random.nextInt(4)]

        _viewState.value = ViewState.Display(
            equation = "$firstValue ${sign.sign} $secondValue = ",
            anchorId = anchorIds[random.nextInt(anchorIds.size)],
            shouldShowSnackbar = points != 0,
            snackbarText = "You've got $points points."
        )
    }

    fun onCheckClicked(result: String) {
        if (result.isBlank()) {
            _viewState.value = ViewState.Warning("Result cannot be empty!")
            return
        }
        val calcResult = when (sign) {
            Equation.ADDITION -> firstValue + secondValue
            Equation.SUBTRACTION -> firstValue - secondValue
            Equation.MULTIPLICATION -> firstValue * secondValue
            Equation.DIVISION -> firstValue / secondValue
        }.toInt()

        if (calcResult == result.toInt()) {
            points += 1
            nextEquation()
        } else {
            points -= 1
            nextEquation()
        }

        if (points == 0) {
            _viewState.value = ViewState.GameOver
        }
    }

    sealed class ViewState {
        data class Display(
            val equation: String,
            val anchorId: Int,
            val shouldShowSnackbar: Boolean,
            val snackbarText: String
        ) : ViewState()

        data class Warning(
            val text: String
        ) : ViewState()

        object GameOver : ViewState()
    }

    enum class Equation(val sign: String) {
        ADDITION("+"),
        SUBTRACTION("-"),
        MULTIPLICATION("x"),
        DIVISION("/")
    }
}