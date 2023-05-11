package fr.isen.bonnefond.jarvisapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import fr.isen.bonnefond.jarvisapp.databinding.ActivityLoginBinding

class LoginActivity() : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    private var enteredPasscode = ""
    private var nb = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val correctPasscode = "1234"

        binding.Btn1.setOnClickListener {
            enteredPasscode += "1"
            checkPasscode(/*enteredPasscode,*/ "1234")
        }

        binding.Btn2.setOnClickListener {
            enteredPasscode += "2"
            checkPasscode(/*enteredPasscode,*/ "1234")
        }

        binding.Btn3.setOnClickListener {
            enteredPasscode += "3"
            checkPasscode(/*enteredPasscode,*/ "1234")

        }

        binding.Btn4.setOnClickListener {
            enteredPasscode += "4"
            checkPasscode(/*enteredPasscode,*/ "1234")

        }

        binding.Btn5.setOnClickListener {
            enteredPasscode += "5"
            checkPasscode(/*enteredPasscode,*/ "1234")

        }

        binding.Btn6.setOnClickListener {
            enteredPasscode += "6"
            checkPasscode(/*enteredPasscode,*/ "1234")

        }

        binding.Btn7.setOnClickListener {
            enteredPasscode += "7"
            checkPasscode(/*enteredPasscode,*/ "1234")

        }
        binding.Btn8.setOnClickListener {
            enteredPasscode += "8"
            checkPasscode(/*enteredPasscode,*/ "1234")

        }
        binding.Btn9.setOnClickListener {
            enteredPasscode += "9"
            checkPasscode(/*enteredPasscode,*/ "1234")

        }
        binding.Btn0.setOnClickListener {
            enteredPasscode += "0"
            checkPasscode(/*enteredPasscode,*/ "1234")

        }

    }


    private fun checkPasscode(correctPasscode: String) {
        if (enteredPasscode.length == correctPasscode.length) {
            nb++
            binding.grey4.setImageResource(R.drawable.round_dark_grey)
            if (enteredPasscode == correctPasscode) {
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                enteredPasscode = ""
                nb = 0
                binding.grey1.setImageResource(R.drawable.round_light_grey)
                binding.grey2.setImageResource(R.drawable.round_light_grey)
                binding.grey3.setImageResource(R.drawable.round_light_grey)
                binding.grey4.setImageResource(R.drawable.round_light_grey)
                Toast.makeText(this, "Code d'accès incorrect", Toast.LENGTH_SHORT).show()
            }
        } else {
            nb++
            when (nb) {
                1 -> binding.grey1.setImageResource(R.drawable.round_dark_grey)
                2 -> binding.grey2.setImageResource(R.drawable.round_dark_grey)
                3 -> binding.grey3.setImageResource(R.drawable.round_dark_grey)
            }
        }
    }

}
