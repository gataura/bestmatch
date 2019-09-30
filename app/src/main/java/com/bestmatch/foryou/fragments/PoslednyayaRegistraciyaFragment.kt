package com.bestmatch.foryou.fragments


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.bestmatch.foryou.db.AppDatabase
import com.bestmatch.foryou.Model.User
import com.bestmatch.foryou.R
import com.bestmatch.foryou.PhotkaActivity
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 *
 */
class PoslednyayaRegistraciyaFragment : Fragment() {

    private lateinit var checkedRadioText: String
    private lateinit var radioGroup: RadioGroup
    private lateinit var radioButton: RadioButton
    lateinit var db: AppDatabase
    private lateinit var nextButton: Button
    private lateinit var agreeCheckBox: CheckBox
    var user = User()
    private var userId = 0
    private lateinit var intent1: Intent

    @SuppressLint("CheckResult")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_the_last_reg, container, false)

        radioGroup = view.findViewById(R.id.radioGroupLasPage)
        db = AppDatabase.getInstance(this.requireContext()) as AppDatabase
        nextButton = view.findViewById(R.id.enter_second_next_btn2)
        agreeCheckBox = view.findViewById(R.id.enter_agreement_checkbox)


        Observable.fromCallable { db.userDao().getLastId() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                userId = it
            }, {
                Log.d("SaveToDb", "Didn't saved in Registration Fragment", it)
            })

        nextButton.setOnClickListener {

            radioButton = view.findViewById(radioGroup.checkedRadioButtonId)
            checkedRadioText = radioButton.text.toString()
            user.setLookFor(checkedRadioText)
            if (agreeCheckBox.isChecked) {
                Completable.fromAction { db.userDao().updateLookFor(user.getLookFor(), userId) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({}, {
                        Log.d("SaveToDb", "Didn't saved in Last Fragment", it)
                    })
                intent1 = Intent(this.requireContext(), PhotkaActivity::class.java)
                startActivity(intent1)

            } else {
                Toast.makeText(this.requireContext(), "Accept terms and conditions", Toast.LENGTH_SHORT).show()
            }

        }

        return view
    }


}
