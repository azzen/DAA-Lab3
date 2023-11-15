package com.example.lab3

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.Group
import com.example.lab3.model.Person
import com.example.lab3.model.Student
import com.example.lab3.model.Worker
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Calendar

/**
 * @authors : Rayane Annen et Hugo Ducommun
 */
class PersonController : AppCompatActivity() {

    // base widgets
    private lateinit var nameEditText: EditText
    private lateinit var firstNameEditText: EditText
    private lateinit var birthDayEditText: EditText
    private lateinit var cakeButton: ImageButton
    private lateinit var nationalitySpinner: Spinner

    private lateinit var occupationRadioGroup: RadioGroup
    private lateinit var studentRadioButton: RadioButton
    private lateinit var workerRadioButton: RadioButton

    // student widgets
    private lateinit var studentGroup: Group
    private lateinit var schoolEditText: EditText
    private lateinit var graduationYearEditText: EditText

    // worker widgets

    private lateinit var workerGroup: Group
    private lateinit var companyEditText: EditText
    private lateinit var sectorSpinner: Spinner
    private lateinit var experienceEditText: EditText

    // Additional information

    private lateinit var additionalTitle: TextView
    private lateinit var emailEditText: EditText
    private lateinit var remarkEditText: EditText

    // Buttons
    private lateinit var cancelButton: Button
    private lateinit var saveButton: Button

    // Setup DatePicker
    private val pickedDate: Calendar = Calendar.getInstance()
    private lateinit var datePickerDialog: DatePickerDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // load widgets
        loadWidgets()

        // manage visibility when radio button is clicked
        handleVisibilityGroup()

        // setup spinner with default text
        setupSpinner(nationalitySpinner, R.string.nationality_empty, R.array.nationalities)
        setupSpinner(sectorSpinner, R.string.sectors_empty, R.array.sectors)

        // setup click events
        cancelButton.setOnClickListener {
            resetGui()
        }

        saveButton.setOnClickListener {
            createPerson()
        }

        cakeButton.setOnClickListener {
            showDatePicker()
        }


        // Load the person with the object passed in argument in the form with its value
        remarkEditText.setOnEditorActionListener { _, actionId, _ ->
         if (actionId == EditorInfo.IME_ACTION_DONE) {
                createPerson()
                true
            }
            false
        }

        // load default example
        loadPerson(Person.exampleWorker)
    }

    // prevent memory leak when changing device orientation
    override fun onDestroy(){
        super.onDestroy();
        if (datePickerDialog != null && datePickerDialog.isShowing()) {
            datePickerDialog.dismiss();
        }
    }

    private fun loadPerson(obj: Person) {
        // load fields based on obj
        nameEditText.setText(obj.name)
        firstNameEditText.setText(obj.firstName)
        setPickedDate(obj.birthDay.get(Calendar.YEAR), obj.birthDay.get(Calendar.MONTH), obj.birthDay.get(Calendar.DAY_OF_MONTH))
        emailEditText.setText(obj.email)
        val nationalities = resources.getStringArray(R.array.nationalities)
        nationalitySpinner.setSelection(nationalities.indexOf(obj.nationality) + 1)

        if (obj is Student) {
            // load student fields
            studentRadioButton.isChecked = true
            studentRadioButton.callOnClick()
            schoolEditText.setText(obj.university)
            graduationYearEditText.setText(obj.graduationYear.toString())
        } else if (obj is Worker) {
            // load worker fields
            val sectors = resources.getStringArray(R.array.sectors)
            sectorSpinner.setSelection(sectors.indexOf(obj.sector) + 1)
            workerRadioButton.isChecked = true
            workerRadioButton.callOnClick()
            companyEditText.setText(obj.company)
            experienceEditText.setText(obj.experienceYear.toString())
        }

        if (obj.remark.isNotEmpty()) {
            remarkEditText.setText(obj.remark)
        }
    }

    // function to create a person based on the form
    private fun createPerson() {
        Log.d("createPerson", nationalitySpinner.selectedItemId.toString())
        // if basic fields are empty, show a toast and return
        if (nameEditText.text.isEmpty() ||
            firstNameEditText.text.isEmpty() ||
            birthDayEditText.text.isEmpty() ||
            nationalitySpinner.selectedItemId < 1 ||
            emailEditText.text.isEmpty()) {
            Toast.makeText(this, resources.getString(R.string.missing_fields), Toast.LENGTH_LONG).show()
            return
        }

        if (studentRadioButton.isChecked) {
            if (schoolEditText.text.isEmpty() ||
                graduationYearEditText.text.isEmpty()) {
                Toast.makeText(this, resources.getString(R.string.missing_fields), Toast.LENGTH_LONG).show()
                return
            }
            val s = Student (
                nameEditText.text.toString(),
                firstNameEditText.text.toString(),
                pickedDate,
                nationalitySpinner.selectedItem.toString(),
                schoolEditText.text.toString(),
                graduationYearEditText.text.toString().toInt(),
                emailEditText.text.toString(),
                remarkEditText.text.toString()
            )
            Log.i("Student", s.toString())
            Toast.makeText(this, resources.getString(R.string.success), Toast.LENGTH_LONG).show()
        } else {
            if (companyEditText.text.isEmpty() ||
                sectorSpinner.selectedItemId < 1 ||
                experienceEditText.text.isEmpty()) {
                Toast.makeText(this, resources.getString(R.string.missing_fields), Toast.LENGTH_LONG).show()
                return
            }

           val w = Worker (
                nameEditText.text.toString(),
                firstNameEditText.text.toString(),
                pickedDate,
                nationalitySpinner.selectedItem.toString(),
                companyEditText.text.toString(),
                sectorSpinner.selectedItem.toString(),
                experienceEditText.text.toString().toInt(),
                emailEditText.text.toString(),
                remarkEditText.text.toString()
            )
            Log.d("Worker", w.toString())
            Toast.makeText(this, resources.getString(R.string.success), Toast.LENGTH_LONG).show()
        }

    }

    private fun loadWidgets() {
        nameEditText = findViewById(R.id.edit_main_base_name_title)
        firstNameEditText = findViewById(R.id.edit_main_base_firstname_title)
        birthDayEditText = findViewById(R.id.edit_main_base_birthday_title)
        cakeButton = findViewById(R.id.btn_main_base_birthday_title)
        nationalitySpinner = findViewById(R.id.spinner_main_base_nationality)

        occupationRadioGroup = findViewById(R.id.radio_group_occupation)
        studentRadioButton = findViewById(R.id.occupation_choice_1)
        workerRadioButton = findViewById(R.id.occupation_choice_2)

        studentGroup = findViewById(R.id.group_Student_Specific)

        schoolEditText = findViewById(R.id.edit_main_specific_school_title)
        graduationYearEditText = findViewById(R.id.edit_main_specific_graduationyear_title)

        workerGroup = findViewById(R.id.group_Worker_Specific)
        companyEditText = findViewById(R.id.edit_main_specific_compagny)
        sectorSpinner = findViewById(R.id.spinner_main_specific_sector)
        experienceEditText = findViewById(R.id.edit_main_specific_experience_title)

        additionalTitle = findViewById(R.id.additional_title)
        emailEditText = findViewById(R.id.edit_additional_email_title)
        remarkEditText = findViewById(R.id.edit_additional_remark_title)

        cancelButton = findViewById(R.id.btn_cancel)
        saveButton = findViewById(R.id.btn_ok)

    }

    // setup a spinner with a default text that is not selectable
    // adapted from https://www.tutorialspoint.com/how-to-make-an-android-spinner-with-initial-default-text
    private fun setupSpinner(spinner: Spinner, defaultTextId: Int, options: Int) {
        val optionsArray = arrayOf(resources.getString(defaultTextId)) + resources.getStringArray(options)
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, optionsArray)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = arrayAdapter
    }

    // reset the form
    private fun resetGui() {
        nameEditText.text.clear()
        firstNameEditText.text.clear()
        birthDayEditText.text.clear()
        nationalitySpinner.setSelection(0)

        occupationRadioGroup.clearCheck()

        schoolEditText.text.clear()

        graduationYearEditText.text.clear()

        companyEditText.text.clear()
        sectorSpinner.setSelection(0)
        experienceEditText.text.clear()

        emailEditText.text.clear()
        remarkEditText.text.clear()

        // set picked date to today
        pickedDate.time = Calendar.getInstance().time

        // hide student and worker group
        studentGroup.visibility = View.GONE
        workerGroup.visibility = View.GONE

   }

    // setup date picker dialog
    private fun showDatePicker() {
        // https://devendrac706.medium.com/date-picker-using-kotlin-in-android-studio-datepickerdialog-android-studio-tutorial-kotlin-3bbc606585a
        // Create a DatePickerDialog
        val listener = DatePickerDialog.OnDateSetListener {
            _, year, month, dayOfMonth -> setPickedDate(year, month, dayOfMonth)
        }
        datePickerDialog = DatePickerDialog(this, listener,
            pickedDate.get(Calendar.YEAR),
            pickedDate.get(Calendar.MONTH),
            pickedDate.get(Calendar.DAY_OF_MONTH))

        datePickerDialog.setTitle(resources.getString(R.string.main_base_birthdate_title))
        // Show the DatePicker dialog
        datePickerDialog.show()
    }


    // define the picked date when selected from the date picker
    private fun setPickedDate(year: Int, month: Int, day: Int) {
        val date = LocalDate.of(year, month + 1, day) // month is 0-indexed
        pickedDate.set(year, month, day)
        val dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        birthDayEditText.setText(dateFormat.format(date))
    }

    // hide or show student and worker group based on radio button
    private fun handleVisibilityGroup() {
        studentGroup.visibility = View.GONE
        workerGroup.visibility = View.GONE

        studentRadioButton.setOnClickListener {
            studentGroup.visibility = View.VISIBLE
            workerGroup.visibility = View.INVISIBLE
        }

        workerRadioButton.setOnClickListener {
            studentGroup.visibility = View.GONE
            workerGroup.visibility = View.VISIBLE
        }
    }
}