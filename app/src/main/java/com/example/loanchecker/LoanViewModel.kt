package com.example.loanchecker

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class LoanViewModel(application: Application) : AndroidViewModel(application) {
    private val formData = mutableMapOf<String, String>()
    private lateinit var tflite: Interpreter

    fun onFieldChanged(field: String, value: String) {
        formData[field] = value
    }

    fun loadModel(context: Context) {
        val fileDescriptor = context.assets.openFd("loan_model.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val modelBuffer: MappedByteBuffer = fileChannel.map(
            FileChannel.MapMode.READ_ONLY,
            fileDescriptor.startOffset,
            fileDescriptor.declaredLength
        )
        tflite = Interpreter(modelBuffer)
    }

    fun predict(): String {
        val input = preprocessInput(formData)
        val output = Array(1) { FloatArray(1) }
        tflite.run(input, output)
        return if (output[0][0] > 0.5f) "Approved" else "Denied"
    }

    private fun preprocessInput(data: Map<String, String>): Array<FloatArray> {
        val inputVector = mutableListOf<Float>()

        // --- numeric fields (in order) ---
        val numericFields = listOf(
            "loan_amnt",
            "term",
            "int_rate",
            "installment",
            "annual_inc",
            "dti",
            "earliest_cr_line",
            "open_acc",
            "pub_rec",
            "revol_bal",
            "revol_util",
            "total_acc",
            "mort_acc",
            "pub_rec_bankruptcies"
        )

        // Process numeric fields
        val numericValues = numericFields.map { field ->
            // Convert the value to a Float or default to 0.0f if not present or invalid
            data[field]?.toFloatOrNull() ?: 0f
        }
        inputVector.addAll(numericValues)

        // --- One-hot encoded fields in exact order  ---
        val oneHotFields = listOf(
            // Sub Grade
            "A2",
            "A3",
            "A4",
            "A5",
            "B1",
            "B2",
            "B3",
            "B4",
            "B5",
            "C1",
            "C2",
            "C3",
            "C4",
            "C5",
            "D1",
            "D2",
            "D3",
            "D4",
            "D5",
            "E1",
            "E2",
            "E3",
            "E4",
            "E5",
            "F1",
            "F2",
            "F3",
            "F4",
            "F5",
            "G1",
            "G2",
            "G3",
            "G4",
            "G5",

            // Verification type
            "verification_status_Source Verified",
            "verification_status_Verified",

            // Application Type
            "application_type_JOINT",
            "application_type_INDIVIDUAL",

            // Initial list status
            "initial_list_status_w",

            // Purpose
            "purpose_credit_card",
            "purpose_debt_consolidation",
            "purpose_educational",
            "purpose_home_improvement",
            "purpose_house",
            "purpose_major_purchase",
            "purpose_medical",
            "purpose_moving",
            "purpose_other",
            "purpose_renewable_energy",
            "purpose_small_business",
            "purpose_vacation",
            "purpose_wedding",

            // Home ownership
            "OTHER",
            "OWN",
            "RENT",

            // Post Index
            "05113",
            "11650",
            "22690",
            "29597",
            "30723",
            "48052",
            "70466",
            "86630",
            "93700"
        )

        // Process the one-hot fields
        for (field in oneHotFields) {
            // Ensure the field exists in the data, default to "false" if not found
            inputVector.add(if ((data[field] ?: "false").toBoolean()) 1f else 0f)
        }

        if (inputVector.size != 78) {
            throw IllegalArgumentException("Input vector size mismatch. Expected 78 features but got ${inputVector.size}.")
        }

        val minValues = floatArrayOf(
            500.0f,
            36.0f,
            5.32f,
            16.08f,
            0.0f,
            0.0f,
            1944.0f,
            1.0f,
            0.0f,
            0.0f,
            0.0f,
            2.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f
        )

        val maxValues = floatArrayOf(
            40000.0f,
            60.0f,
            30.99f,
            1533.81f,
            8706582.0f,
            9999.0f,
            2013.0f,
            90.0f,
            86.0f,
            1743266.0f,
            892.3f,
            150.0f,
            34.0f,
            8.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f
        )

        // --- Scale all features ---
        val means = FloatArray(minValues.size) { i -> (minValues[i] + maxValues[i]) / 2 }
        val stds = FloatArray(minValues.size) { i -> (maxValues[i] - minValues[i]) / 4 }

        // Scale the input vector
        val scaledInput = inputVector.mapIndexed { i, value ->
            (value - means[i]) / stds[i]
        }

        // Return the scaled input in a 2D array format
        return arrayOf(scaledInput.toFloatArray())
    }

}
