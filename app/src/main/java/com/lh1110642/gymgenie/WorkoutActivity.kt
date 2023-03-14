package com.lh1110642.gymgenie

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.lh1110642.gymgenie.databinding.ActivityWorkoutsBinding
import kotlinx.coroutines.delay


var listworkOutOne: ArrayList<Exercise> = ArrayList()
var listworkOutTwo: ArrayList<Exercise> = ArrayList()
var listworkOutThree: ArrayList<Exercise> = ArrayList()
var listworkOutFour: ArrayList<Exercise> = ArrayList()
var refresh = "false";
lateinit var workoutOne: ArrayList<String>
lateinit var workoutTwo: ArrayList<String>
lateinit var workoutThree: ArrayList<String>
lateinit var workoutFour: ArrayList<String>

var DBlistExercise = arrayOfNulls<Exercise>(1000)
class WorkoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWorkoutsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkoutsBinding.inflate(layoutInflater)
        setContentView(binding.root)
         refresh = intent.getStringExtra("refresh").toString()
        if(refresh == "true"){
            startActivity(Intent(this, WorkoutActivity::class.java).putExtra("refresh", false))
        }


        //list view setup
        workoutOne = ArrayList()
        workoutTwo = ArrayList()
        workoutThree = ArrayList()
        workoutFour = ArrayList()

        listworkOutOne.clear()
        workoutOne.clear()
        listworkOutTwo.clear()
        workoutTwo.clear()
        listworkOutThree.clear()
        workoutThree.clear()
        listworkOutFour.clear()
        workoutFour.clear()


        val adapter: ArrayAdapter<String?> = ArrayAdapter<String?>(
            this@WorkoutActivity,
            android.R.layout.simple_list_item_1,
            workoutOne as List<String?>
        )
        val adapter2: ArrayAdapter<String?> = ArrayAdapter<String?>(
            this@WorkoutActivity,
            android.R.layout.simple_list_item_1,
            workoutTwo as List<String?>
        )
        val adapter3: ArrayAdapter<String?> = ArrayAdapter<String?>(
            this@WorkoutActivity,
            android.R.layout.simple_list_item_1,
            workoutThree as List<String?>
        )
        val adapter4: ArrayAdapter<String?> = ArrayAdapter<String?>(
            this@WorkoutActivity,
            android.R.layout.simple_list_item_1,
            workoutFour as List<String?>
        )
        binding.lvOne.adapter = adapter
        binding.lvTwo.adapter = adapter2
        binding.lvThree.adapter = adapter3
        binding.lvFour.adapter = adapter4

        binding.lvOne.setOnItemClickListener { parent, view, position, id ->
            val myIntent: Intent = Intent(view.context,ViewExerciseActivity::class.java).putExtra("muscle", listworkOutOne[position].muscle).putExtra("equipment",listworkOutOne[position].equipment).putExtra("difficulty",listworkOutOne[position].difficulty).putExtra("type", listworkOutOne[position].type).putExtra("description", listworkOutOne[position].instructions).putExtra("name", listworkOutOne[position].name)
            view.context.startActivity(myIntent)

        }
        binding.lvTwo.setOnItemClickListener { parent, view, position, id ->
            val myIntent: Intent = Intent(view.context,ViewExerciseActivity::class.java).putExtra("muscle", listworkOutTwo[position].muscle).putExtra("equipment",listworkOutTwo[position].equipment).putExtra("difficulty",listworkOutTwo[position].difficulty).putExtra("type", listworkOutTwo[position].type).putExtra("description", listworkOutTwo[position].instructions).putExtra("name", listworkOutTwo[position].name)
            view.context.startActivity(myIntent)

        }
        binding.lvThree.setOnItemClickListener { parent, view, position, id ->
            val myIntent: Intent = Intent(view.context,ViewExerciseActivity::class.java).putExtra("muscle", listworkOutThree[position].muscle).putExtra("equipment",listworkOutThree[position].equipment).putExtra("difficulty",listworkOutThree[position].difficulty).putExtra("type", listworkOutThree[position].type).putExtra("description", listworkOutThree[position].instructions).putExtra("name", listworkOutThree[position].name)
            view.context.startActivity(myIntent)

        }
        binding.lvFour.setOnItemClickListener { parent, view, position, id ->
            val myIntent: Intent = Intent(view.context,ViewExerciseActivity::class.java).putExtra("muscle", listworkOutFour[position].muscle).putExtra("equipment",listworkOutFour[position].equipment).putExtra("difficulty",listworkOutFour[position].difficulty).putExtra("type", listworkOutFour[position].type).putExtra("description", listworkOutFour[position].instructions).putExtra("name", listworkOutFour[position].name)
            view.context.startActivity(myIntent)

        }


        //tracks and updates the listviews
        val viewModel : exerciseViewModel by viewModels()
        viewModel.getExercises().observe(this) { exercises ->

//            for (i in exercises.indices) {
////                //Populated DBListExercise
//                DBlistExercise[i] = exercises[i]
//
//
//            }

            var name = " "
            for (i in exercises.indices) {
                //prints the tokens
                name = exercises[i]?.getName().toString();
                var workOutGroup = exercises[i]?.getWorkOutGroup()

                if (workOutGroup == "workOutOne" && listworkOutOne.contains(exercises[i]) == false){
                    listworkOutOne.add(exercises[i])}
                else if (workOutGroup == "workOutTwo"  && listworkOutTwo.contains(exercises[i]) == false){
                    listworkOutTwo.add(exercises[i])}

                else if (workOutGroup == "workOutThree"  && listworkOutThree.contains(exercises[i]) == false){
                    listworkOutThree.add(exercises[i])}
                else if (workOutGroup == "workOutFour"  && listworkOutFour.contains(exercises[i]) == false){
                    listworkOutFour.add(exercises[i])}


            }

            //sorts and populates each listview
            for (i in listworkOutOne.indices) {
                workoutOne.add(listworkOutOne[i].name)
                adapter.notifyDataSetChanged()
                //workoutTwo.add("Fries")
            }
            for (i in listworkOutTwo.indices) {

                workoutTwo.add(listworkOutTwo[i].name)
                adapter2.notifyDataSetChanged()
                //workoutTwo.add("Fries")

            }
            for (i in listworkOutThree.indices) {
                workoutThree.add(listworkOutThree[i].name)
                adapter3.notifyDataSetChanged()
            }
            for (i in listworkOutFour.indices) {
                workoutFour.add(listworkOutFour[i].name)
                adapter4.notifyDataSetChanged()
            }


            //clears workout groups
        binding.btnClearOne.setOnClickListener{

            //db setup
            val userId = Firebase.auth.currentUser?.uid
            val db = FirebaseFirestore.getInstance().collection("workout")

            for (i in listworkOutOne.indices) {//for each object in the list

                db.document(listworkOutOne[i].name + "workOutOne" + userId)//delete this document name
                    .delete()
                    .addOnSuccessListener { Log.d(ContentValues.TAG, "DB_DELETE COMPLETE") }
                    .addOnFailureListener { e ->
                        Log.w(
                            ContentValues.TAG,
                            "Error deleting document",
                            e
                        )


                    }

            }
            listworkOutOne.clear()
            workoutOne.clear()
            listworkOutTwo.clear()
            workoutTwo.clear()
            listworkOutThree.clear()
            workoutThree.clear()
            listworkOutFour.clear()
            workoutFour.clear()
            startActivity(Intent(this, WorkoutActivity::class.java))
        }
            binding.btnClearTwo.setOnClickListener{

                val userId = Firebase.auth.currentUser?.uid
                val db = FirebaseFirestore.getInstance().collection("workout")

                for (i in listworkOutTwo.indices) {

                    db.document(listworkOutTwo[i].name + "workOutTwo" + userId)
                        .delete()
                        .addOnSuccessListener { Log.d(ContentValues.TAG, "DB_DELETE COMPLETE") }
                        .addOnFailureListener { e ->
                            Log.w(
                                ContentValues.TAG,
                                "Error deleting document",
                                e
                            )


                        }
                }
                listworkOutOne.clear()
                workoutOne.clear()
                listworkOutTwo.clear()
                workoutTwo.clear()
                listworkOutThree.clear()
                workoutThree.clear()
                listworkOutFour.clear()
                workoutFour.clear()
                startActivity(Intent(this, WorkoutActivity::class.java))
            }
            binding.btnClearThree.setOnClickListener{

                val userId = Firebase.auth.currentUser?.uid
                val db = FirebaseFirestore.getInstance().collection("workout")

                for (i in listworkOutThree.indices) {

                    db.document(listworkOutThree[i].name + "workOutThree" + userId)
                        .delete()
                        .addOnSuccessListener { Log.d(ContentValues.TAG, "DB_DELETE COMPLETE") }
                        .addOnFailureListener { e ->
                            Log.w(
                                ContentValues.TAG,
                                "Error deleting document",
                                e
                            )


                        }
                }
                listworkOutOne.clear()
                workoutOne.clear()
                listworkOutTwo.clear()
                workoutTwo.clear()
                listworkOutThree.clear()
                workoutThree.clear()
                listworkOutFour.clear()
                workoutFour.clear()
                startActivity(Intent(this, WorkoutActivity::class.java))

            }
            binding.btnClearFour.setOnClickListener{

                val userId = Firebase.auth.currentUser?.uid
                val db = FirebaseFirestore.getInstance().collection("workout")

                for (i in listworkOutFour.indices) {

                    db.document(listworkOutFour[i].name + "workOutFour" + userId)
                        .delete()
                        .addOnSuccessListener { Log.d(ContentValues.TAG, "DB_DELETE COMPLETE") }
                        .addOnFailureListener { e ->
                            Log.w(
                                ContentValues.TAG,
                                "Error deleting document",
                                e
                            )


                        }
                }
                listworkOutOne.clear()
                workoutOne.clear()
                listworkOutTwo.clear()
                workoutTwo.clear()
                listworkOutThree.clear()
                workoutThree.clear()
                listworkOutFour.clear()
                workoutFour.clear()
                startActivity(Intent(this, WorkoutActivity::class.java).putExtra("refresh", "true"))

            }

        }

    }

    //

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //different menu options
        return when (item.itemId) {
            R.id.anatomy -> {
                startActivity(Intent(this,BrowsingActivity::class.java))
                return true
            }
            R.id.workout -> {
                startActivity(Intent(this,WorkoutActivity::class.java))
                return true
            }
            R.id.profile -> {
                startActivity(Intent(this,ProfileActivity::class.java))
                return true
            }
            R.id.signOut -> {
                AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener {
                        startActivity(Intent(this,LoginActivity::class.java))
                    }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }

        return super.onOptionsItemSelected(item)
    }


}