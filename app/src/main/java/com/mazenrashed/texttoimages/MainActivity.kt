package com.mazenrashed.texttoimages

import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.mazenrashed.txttoimg.ImageHelper
import com.mazenrashed.txttoimg.Line
import com.mazenrashed.txttoimg.TextInLine
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }

    override fun onStart() {
        super.onStart()
        Handler().postDelayed({
            var texts = ArrayList<Line>()
            texts.add(
                Line().apply {

                    this.centerText = TextInLine().apply {
                        this.text = "Receipt"
                        this.color = Color.BLACK
                        this.size = 17f
                    }

                }
            )
            texts.add(
                Line().apply {

                    this.centerText = TextInLine().apply {
                        this.text = "Some Market"
                        this.color = Color.BLACK
                        this.size = 17f
                    }

                }
            )

            texts.add(
                Line().apply {
                    this.leftText = TextInLine().apply {
                        this.text = "Tax No. 23344"
                        this.color = Color.BLACK
                        this.size = 13f
                    }
                    this.rightText = TextInLine().apply {
                        this.text = "11/6/2020 20:16"
                        this.color = Color.BLACK
                        this.size = 13f
                    }

                }
            )

            texts.add(
                Line().apply {
                    this.leftText = TextInLine().apply {
                        this.text = "Cash 3"
                        this.color = Color.BLACK
                        this.size = 13f
                    }
                    this.rightText = TextInLine().apply {
                        this.text = "Payment type: Credit"
                        this.color = Color.BLACK
                        this.size = 13f
                    }

                }
            )
            texts.add(
                Line().apply {
                    this.centerText = TextInLine().apply {
                        this.text = "-----------------------------------"
                        this.color = Color.BLACK
                        this.size = 13f
                    }

                }
            )

            texts.add(
                Line().apply {
                    this.leftText = TextInLine().apply {
                        this.text = "Item"
                        this.color = Color.BLACK
                        this.size = 15f
                    }
                    this.centerText = TextInLine().apply {
                        this.text = "Qty"
                        this.color = Color.BLACK
                        this.size = 15f
                    }
                    this.rightText= TextInLine().apply {
                        this.text = "Price"
                        this.color = Color.BLACK
                        this.size = 15f
                    }

                }
            )
            texts.add(
                Line().apply {
                    this.centerText = TextInLine().apply {
                        this.text = "-----------------------------------"
                        this.color = Color.BLACK
                        this.size = 13f
                    }

                }
            )

            texts.add(
                Line().apply {
                    this.leftText = TextInLine().apply {
                        this.text = "Hypex"
                        this.color = Color.BLACK
                        this.size = 13f
                    }
                    this.centerText = TextInLine().apply {
                        this.text = "2"
                        this.color = Color.BLACK
                        this.size = 13f
                    }
                    this.rightText= TextInLine().apply {
                        this.text = "4.3 JOD"
                        this.color = Color.BLACK
                        this.size = 13f
                    }

                }
            )

            texts.add(
                Line().apply {

                    this.leftText = TextInLine().apply {
                        this.text = "Mr.Chips"
                        this.color = Color.BLACK
                        this.size = 13f
                    }
                    this.centerText = TextInLine().apply {
                        this.text = "4"
                        this.color = Color.BLACK
                        this.size = 13f
                    }
                    this.rightText= TextInLine().apply {
                        this.text = "8.3 JOD"
                        this.color = Color.BLACK
                        this.size = 13f
                    }


                }
            )

            texts.add(
                Line().apply {
                    this.leftText = TextInLine().apply {
                        this.text = "Salt"
                        this.color = Color.BLACK
                        this.size = 13f
                    }
                    this.centerText = TextInLine().apply {
                        this.text = "1"
                        this.color = Color.BLACK
                        this.size = 13f
                    }
                    this.rightText= TextInLine().apply {
                        this.text = "2.1 JOD"
                        this.color = Color.BLACK
                        this.size = 13f
                    }

                }
            )

            texts.add(
                Line().apply {
                    this.centerText = TextInLine().apply {
                        this.text = "-----------------------------------"
                        this.color = Color.BLACK
                        this.size = 13f
                    }

                }
            )
            texts.add(
                Line().apply {
                    this.leftText = TextInLine().apply {
                        this.text = "TOTAL"
                        this.color = Color.BLACK
                        this.size = 13f
                    }
                    this.rightText= TextInLine().apply {
                        this.text = "15.1 JOD"
                        this.color = Color.BLACK
                        this.size = 13f
                    }

                }
            )
            texts.add(
                Line().apply {
                    this.centerText = TextInLine().apply {
                        this.text = "Thank you for shoping"
                        this.color = Color.BLACK
                        this.size = 17f
                    }

                }
            )




            ImageHelper(resources,this).textToBitmap(texts)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    imageView.setImageBitmap(it)
                },{
                    it.printStackTrace()
                })
        },1000)
    }
}
