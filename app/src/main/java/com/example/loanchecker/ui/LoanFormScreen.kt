package com.example.loanchecker.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.loanchecker.LoanViewModel

@Composable
fun LoanFormScreen(viewModel: LoanViewModel) {
    val context = LocalContext.current

    // Map of field names to user-friendly labels
    val fieldLabels = mapOf(
        // Numeric fields
        "loan_amnt" to "Loan Amount ($)",
        "term" to "Loan Term (months)",
        "int_rate" to "Interest Rate (%)",
        "installment" to "Monthly Installment ($)",
        "annual_inc" to "Annual Income ($)",
        "dti" to "Debt-to-Income Ratio (%)",
        "earliest_cr_line" to "Earliest Credit Line (year)",
        "open_acc" to "Open Accounts",
        "pub_rec" to "Public Records",
        "revol_bal" to "Revolving Balance ($)",
        "revol_util" to "Revolving Utilization (%)",
        "total_acc" to "Total Accounts",
        "mort_acc" to "Mortgage Accounts",
        "pub_rec_bankruptcies" to "Public Record Bankruptcies",
        // Selectable fields
        "Post Index" to "Postal Code",
        "Home Owner" to "Home Ownership",
        "Sub Grade" to "Loan Sub-Grade",
        "Verification Status" to "Verification Status",
        "Application Type" to "Application Type",
        "Initial List Status" to "Initial List Status",
        "Purpose" to "Loan Purpose"
    )

    // Map of raw select values to user-friendly display names
    val optionLabels = mapOf(
        // Post Index
        "05113" to "05113",
        "11650" to "11650",
        "22690" to "22690",
        "29597" to "29597",
        "30723" to "30723",
        "48052" to "48052",
        "70466" to "70466",
        "86630" to "86630",
        "93700" to "93700",
        // Home Ownership
        "OTHER" to "Other",
        "OWN" to "Own",
        "RENT" to "Rent",
        // Sub Grade (simplified to show grade only)
        "A2" to "A2", "A3" to "A3", "A4" to "A4", "A5" to "A5",
        "B1" to "B1", "B2" to "B2", "B3" to "B3", "B4" to "B4", "B5" to "B5",
        "C1" to "C1", "C2" to "C2", "C3" to "C3", "C4" to "C4", "C5" to "C5",
        "D1" to "D1", "D2" to "D2", "D3" to "D3", "D4" to "D4", "D5" to "D5",
        "E1" to "E1", "E2" to "E2", "E3" to "E3", "E4" to "E4", "E5" to "E5",
        "F1" to "F1", "F2" to "F2", "F3" to "F3", "F4" to "F4", "F5" to "F5",
        "G1" to "G1", "G2" to "G2", "G3" to "G3", "G4" to "G4", "G5" to "G5",
        // Verification Status
        "verification_status_Source Verified" to "Source Verified",
        "verification_status_Verified" to "Verified",
        // Application Type
        "application_type_JOINT" to "Joint",
        "application_type_INDIVIDUAL" to "Individual",
        // Initial List Status
        "initial_list_status_w" to "Whole",
        // Purpose
        "purpose_credit_card" to "Credit Card",
        "purpose_debt_consolidation" to "Debt Consolidation",
        "purpose_educational" to "Educational",
        "purpose_home_improvement" to "Home Improvement",
        "purpose_house" to "House",
        "purpose_major_purchase" to "Major Purchase",
        "purpose_medical" to "Medical",
        "purpose_moving" to "Moving",
        "purpose_other" to "Other",
        "purpose_renewable_energy" to "Renewable Energy",
        "purpose_small_business" to "Small Business",
        "purpose_vacation" to "Vacation",
        "purpose_wedding" to "Wedding"
    )

    val selectableFields = mapOf(
        "Post Index" to listOf("05113", "11650", "22690", "29597", "30723", "48052", "70466", "86630", "93700"),
        "Home Owner" to listOf("OTHER", "OWN", "RENT"),
        "Sub Grade" to listOf("A2", "A3", "A4", "A5", "B1", "B2", "B3", "B4", "B5", "C1", "C2", "C3", "C4", "C5", "D1", "D2", "D3", "D4", "D5", "E1", "E2", "E3", "E4", "E5", "F1", "F2", "F3", "F4", "F5", "G1", "G2", "G3", "G4", "G5"),
        "Verification Status" to listOf("verification_status_Source Verified", "verification_status_Verified"),
        "Application Type" to listOf("application_type_JOINT", "application_type_INDIVIDUAL"),
        "Purpose" to listOf("purpose_credit_card", "purpose_debt_consolidation", "purpose_educational", "purpose_home_improvement",
            "purpose_house", "purpose_major_purchase", "purpose_medical", "purpose_moving", "purpose_other",
            "purpose_renewable_energy", "purpose_small_business", "purpose_vacation", "purpose_wedding")
    )

    val numericFields = listOf(
        "loan_amnt", "term", "int_rate", "installment", "annual_inc", "dti", "earliest_cr_line",
        "open_acc", "pub_rec", "revol_bal", "revol_util", "total_acc", "mort_acc", "pub_rec_bankruptcies"
    )

    // State for showing dialog
    var showDialog by remember { mutableStateOf(false) }
    var predictionResult by remember { mutableStateOf("") }

    // Make the content scrollable
    Column(modifier = Modifier
        .padding(16.dp)
        .verticalScroll(rememberScrollState())) {

        // Render selectable fields (excluding Initial List Status)
        selectableFields.forEach { (label, options) ->
            DropdownSelector(
                fieldLabels[label] ?: label,
                options,
                optionLabels,
                viewModel::onFieldChanged
            )
        }

        // Render checkbox for Initial List Status
        CheckboxField(
            label = fieldLabels["Initial List Status"] ?: "Initial List Status",
            onCheckedChange = { isChecked ->
                viewModel.onFieldChanged("initial_list_status_w", isChecked.toString())
            }
        )

        // Render numeric input fields
        numericFields.forEach { label ->
            NumericInputField(
                fieldLabels[label] ?: label,
                viewModel::onFieldChanged
            )
        }

        // Calculate button
        Button(
            onClick = {
                viewModel.loadModel(context)
                predictionResult = viewModel.predict()
                showDialog = true
            },
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
        ) {
            Text("Check Credit")
        }
    }

    // AlertDialog for showing prediction result
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Loan Application Result") },
            text = { Text("Your loan application is: $predictionResult") },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun DropdownSelector(
    label: String,
    options: List<String>,
    optionLabels: Map<String, String>,
    onValueSelected: (String, String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("") }

    Column(Modifier.padding(vertical = 4.dp)) {
        Text(label)
        Box(modifier = Modifier.clickable { expanded = true }.padding(8.dp)) {
            Text(text = if (selectedOption.isEmpty()) "Select $label" else (optionLabels[selectedOption] ?: selectedOption))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(onClick = {
                    selectedOption = option
                    onValueSelected(label, option) // Pass raw value to ViewModel
                    expanded = false
                }) {
                    Text(optionLabels[option] ?: option)
                }
            }
        }
    }
}

@Composable
fun CheckboxField(
    label: String,
    onCheckedChange: (Boolean) -> Unit
) {
    var checked by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = {
                checked = it
                onCheckedChange(it)
            }
        )
        Text(
            text = label,
            modifier = Modifier.padding(start = 8.dp, top = 12.dp)
        )
    }
}

@Composable
fun NumericInputField(label: String, onValueEntered: (String, String) -> Unit) {
    var value by remember { mutableStateOf("") }
    OutlinedTextField(
        value = value,
        onValueChange = {
            value = it
            onValueEntered(label, it)
        },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
}