package com.example.gridgenerator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gridgenerator.ui.theme.GridGeneratorTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GridGeneratorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(navController)
        }
        composable("hobby") {
            HobbyScreen(navController)
        }
        composable("input") {
            InputScreen(navController)
        }
        composable("grid/{width}/{height}") { backStackEntry ->
            val width = backStackEntry.arguments?.getString("width")?.toIntOrNull() ?: 1
            val height = backStackEntry.arguments?.getString("height")?.toIntOrNull() ?: 1
            GridScreen(navController, width, height)
        }
        composable("drone_calculator") {
            DroneCalculatorScreen(navController)
        }
        composable("drone_motor_calculator") {
            DroneMotorCalculatorScreen(navController)
        }
    }
}

@Composable
fun MainScreen(navController: NavController) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("hobby") },
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DiyIcon()
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Hobby", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HobbyScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hobby") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("input") },
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GridPreviewImage()
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Greenhouse/grow tent", style = MaterialTheme.typography.titleMedium)
                            Text("plant layout optimization", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("drone_calculator") },
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DroneIcon()
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Drone Flight Time Calculator", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("drone_motor_calculator") },
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MotorIcon()
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Drone Motor & Thrust Calculator", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun MotorIcon() {
    Canvas(modifier = Modifier.size(64.dp)) {
        drawCircle(color = Color.DarkGray, radius = size.minDimension / 4)
        drawRect(
            color = Color.Gray,
            topLeft = Offset(x = center.x - size.width / 8, y = center.y - size.height / 2),
            size = Size(size.width / 4, size.height)
        )
    }
}


@Composable
fun DiyIcon() {
    Canvas(modifier = Modifier.size(64.dp)) {
        // Simple hammer icon
        val headWidth = size.width * 0.6f
        val headHeight = size.height * 0.3f
        drawRect(
            color = Color.Gray,
            topLeft = Offset(x = size.width * 0.2f, y = 0f),
            size = Size(headWidth, headHeight)
        )
        val handleWidth = size.width * 0.2f
        val handleHeight = size.height * 0.7f
        drawRect(
            color = Color(0xFF8B4513), // SaddleBrown
            topLeft = Offset(x = center.x - handleWidth / 2, y = headHeight),
            size = Size(handleWidth, handleHeight)
        )
    }
}

@Composable
fun DroneIcon() {
    Canvas(modifier = Modifier.size(64.dp)) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val bodySize = size.width / 3
        drawRect(
            color = Color.DarkGray,
            topLeft = Offset(centerX - bodySize / 2, centerY - bodySize / 2),
            size = Size(bodySize, bodySize)
        )
        // Arms
        drawLine(Color.DarkGray, Offset(centerX, centerY), Offset(0f, 0f), strokeWidth = 8f)
        drawLine(Color.DarkGray, Offset(centerX, centerY), Offset(size.width, 0f), strokeWidth = 8f)
        drawLine(Color.DarkGray, Offset(centerX, centerY), Offset(0f, size.height), strokeWidth = 8f)
        drawLine(Color.DarkGray, Offset(centerX, centerY), Offset(size.width, size.height), strokeWidth = 8f)
    }
}

@Composable
fun GridPreviewImage() {
    Canvas(modifier = Modifier.size(64.dp)) {
        val cellSize = size.width / 4
        // Grid lines
        for (i in 1 until 4) {
            drawLine(
                color = Color.Gray,
                start = Offset(x = i * cellSize, y = 0f),
                end = Offset(x = i * cellSize, y = size.height)
            )
            drawLine(
                color = Color.Gray,
                start = Offset(x = 0f, y = i * cellSize),
                end = Offset(x = size.width, y = i * cellSize)
            )
        }

        // Plants
        drawRect(
            color = Color.Green,
            topLeft = Offset(x = 0.5f * cellSize, y = 0.5f * cellSize),
            size = Size(cellSize * 0.8f, cellSize * 0.8f)
        )
        drawRect(
            color = Color.Green,
            topLeft = Offset(x = 2.5f * cellSize, y = 2.5f * cellSize),
            size = Size(cellSize * 0.8f, cellSize * 0.8f)
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DroneMotorCalculatorScreen(navController: NavController) {
    var droneWeight by remember { mutableStateOf(TextFieldValue("")) }
    var droneWeightUnit by remember { mutableStateOf("lb") }
    var batteryWeight by remember { mutableStateOf(TextFieldValue("")) }
    var batteryWeightUnit by remember { mutableStateOf("lb") }
    var equipmentWeight by remember { mutableStateOf(TextFieldValue("")) }
    var equipmentWeightUnit by remember { mutableStateOf("lb") }
    var totalWeight by remember { mutableStateOf(TextFieldValue("")) }
    var totalWeightUnit by remember { mutableStateOf("lb") }
    var thrustRatio by remember { mutableStateOf(TextFieldValue("2")) }
    var numMotors by remember { mutableStateOf(TextFieldValue("4")) }

    var totalThrust by remember { mutableStateOf<Float?>(null) }
    var thrustPerMotor by remember { mutableStateOf<Float?>(null) }
    var resultUnit by remember { mutableStateOf("g") }

    val weightUnits = listOf("g", "kg", "lb", "oz")

    fun convertToGrams(value: Float, unit: String): Float {
        return when (unit) {
            "kg" -> value * 1000
            "lb" -> value * 453.592f
            "oz" -> value * 28.3495f
            else -> value
        }
    }

    fun convertFromGrams(value: Float, unit: String): Float {
        return when (unit) {
            "kg" -> value / 1000
            "lb" -> value / 453.592f
            "oz" -> value / 28.3495f
            else -> value
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Drone Motor & Thrust Calculator") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item { Text("Enter individual weights or total weight:", style = MaterialTheme.typography.titleMedium) }
            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                UnitInputField("Drone Weight", droneWeight, droneWeightUnit, weightUnits) { value, unit ->
                    droneWeight = value
                    droneWeightUnit = unit
                }
            }
            item {
                UnitInputField("Battery Weight", batteryWeight, batteryWeightUnit, weightUnits) { value, unit ->
                    batteryWeight = value
                    batteryWeightUnit = unit
                }
            }
            item {
                UnitInputField("Equipment Weight", equipmentWeight, equipmentWeightUnit, weightUnits) { value, unit ->
                    equipmentWeight = value
                    equipmentWeightUnit = unit
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item { Text("OR", style = MaterialTheme.typography.titleMedium) }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                UnitInputField("Total Weight", totalWeight, totalWeightUnit, weightUnits) { value, unit ->
                    totalWeight = value
                    totalWeightUnit = unit
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                TextField(
                    value = thrustRatio,
                    onValueChange = { thrustRatio = it },
                    label = { Text("Thrust-to-weight ratio") }
                )
            }
            item {
                TextField(
                    value = numMotors,
                    onValueChange = { numMotors = it },
                    label = { Text("Number of motors") }
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                Button(onClick = {
                    val droneW = convertToGrams(droneWeight.text.toFloatOrNull() ?: 0f, droneWeightUnit)
                    val batteryW = convertToGrams(batteryWeight.text.toFloatOrNull() ?: 0f, batteryWeightUnit)
                    val equipmentW = convertToGrams(equipmentWeight.text.toFloatOrNull() ?: 0f, equipmentWeightUnit)
                    val totalW = totalWeight.text.toFloatOrNull()

                    val finalTotalWeightInGrams = if (totalW != null && totalW > 0) {
                        convertToGrams(totalW, totalWeightUnit)
                    } else {
                        droneW + batteryW + equipmentW
                    }

                    val ratio = thrustRatio.text.toFloatOrNull() ?: 2f
                    val motors = numMotors.text.toIntOrNull() ?: 4

                    if (finalTotalWeightInGrams > 0 && motors > 0) {
                        val totalThrustGrams = finalTotalWeightInGrams * ratio
                        val thrustPerMotorGrams = totalThrustGrams / motors
                        totalThrust = totalThrustGrams
                        thrustPerMotor = thrustPerMotorGrams
                    } else {
                        totalThrust = null
                        thrustPerMotor = null
                    }
                }) {
                    Text("Calculate")
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                totalThrust?.let {
                    val convertedThrust = convertFromGrams(it, resultUnit)
                    Text("Total Thrust: %.2f ${resultUnit}".format(convertedThrust))
                }
            }
            item {
                thrustPerMotor?.let {
                    val convertedThrust = convertFromGrams(it, resultUnit)
                    Text("Thrust per Motor: %.2f ${resultUnit}".format(convertedThrust))
                }
            }
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Result Unit:")
                    Spacer(modifier = Modifier.width(8.dp))
                    UnitDropdown(resultUnit, weightUnits) { resultUnit = it }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitInputField(
    label: String,
    value: TextFieldValue,
    selectedUnit: String,
    units: List<String>,
    onValueChange: (TextFieldValue, String) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        TextField(
            value = value,
            onValueChange = { onValueChange(it, selectedUnit) },
            label = { Text(label) },
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        UnitDropdown(selectedUnit, units) { onValueChange(value, it) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitDropdown(
    selectedUnit: String,
    units: List<String>,
    onUnitSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.width(100.dp)
    ) {
        TextField(
            value = selectedUnit,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            units.forEach { unit ->
                DropdownMenuItem(
                    text = { Text(unit) },
                    onClick = {
                        onUnitSelected(unit)
                        expanded = false
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DroneCalculatorScreen(navController: NavController) {
    var batteryCapacity by remember { mutableStateOf(TextFieldValue("")) }
    var capacityUnit by remember { mutableStateOf("Ah") }
    var allUpWeight by remember { mutableStateOf(TextFieldValue("")) }
    var weightUnit by remember { mutableStateOf("lb") }
    var batteryVoltage by remember { mutableStateOf(TextFieldValue("")) }
    var flightTime by remember { mutableStateOf<Float?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Drone Flight Time Calculator") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = batteryCapacity,
                onValueChange = { batteryCapacity = it },
                label = { Text("Battery Capacity") }
            )
            Row {
                RadioButton(selected = capacityUnit == "Ah", onClick = { capacityUnit = "Ah" })
                Text("Ah", modifier = Modifier.align(Alignment.CenterVertically))
                RadioButton(selected = capacityUnit == "mAh", onClick = { capacityUnit = "mAh" })
                Text("mAh", modifier = Modifier.align(Alignment.CenterVertically))
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = allUpWeight,
                onValueChange = { allUpWeight = it },
                label = { Text("All-Up Weight") }
            )
            Row {
                RadioButton(selected = weightUnit == "lb", onClick = { weightUnit = "lb" })
                Text("lb", modifier = Modifier.align(Alignment.CenterVertically))
                RadioButton(selected = weightUnit == "kg", onClick = { weightUnit = "kg" })
                Text("kg", modifier = Modifier.align(Alignment.CenterVertically))
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = batteryVoltage,
                onValueChange = { batteryVoltage = it },
                label = { Text("Battery Voltage (V)") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                val capacityValue = batteryCapacity.text.toFloatOrNull()
                val weightValue = allUpWeight.text.toFloatOrNull()
                val voltageV = batteryVoltage.text.toFloatOrNull()
                val powerRequiredToLift = 170f // W/kg
                val batteryDischarge = 1f // 100%

                if (capacityValue != null && weightValue != null && voltageV != null && voltageV > 0) {
                    val capacityAh = if (capacityUnit == "mAh") capacityValue / 1000 else capacityValue
                    val weightKg = if (weightUnit == "lb") weightValue * 0.453592f else weightValue
                    
                    val avgAmpDraw = (weightKg * powerRequiredToLift) / voltageV
                    if (avgAmpDraw > 0) {
                        val timeHours = (capacityAh * batteryDischarge) / avgAmpDraw
                        flightTime = timeHours * 60
                    } else {
                        flightTime = null
                    }
                } else {
                    flightTime = null
                }
            }) {
                Text("Calculate")
            }
            Spacer(modifier = Modifier.height(16.dp))
            flightTime?.let {
                Text("Estimated Flight Time: %.2f minutes".format(it))
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputScreen(navController: NavController) {
    var width by remember { mutableStateOf(TextFieldValue("10")) }
    var height by remember { mutableStateOf(TextFieldValue("10")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Input") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = width,
                onValueChange = { width = it },
                label = { Text("Width") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = height,
                onValueChange = { height = it },
                label = { Text("Height") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                navController.navigate("grid/${width.text}/${height.text}")
            }) {
                Text("Generate")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GridScreen(navController: NavController, width: Int, height: Int) {
    val highlightedCells = remember { mutableStateListOf<Int>() }
    val plantCells = remember { mutableStateListOf<Int>() }
    var isLoading by remember { mutableStateOf(false) }
    var attemptsCount by remember { mutableStateOf(0) }
    var triggerLayoutGen by remember { mutableStateOf(0) }
    var triggerExtremeLayoutGen by remember { mutableStateOf(0) }
    var densityRequirement by remember { mutableStateOf(0.5f) }

    if (triggerLayoutGen > 0) {
        LaunchedEffect(triggerLayoutGen) {
            isLoading = true
            attemptsCount = 0
            var foundLayout = false
            
            val densityTarget = (width * height * densityRequirement).toInt()

            while (attemptsCount < 1000 && !foundLayout) {
                attemptsCount++
                val newPlantCells = withContext(Dispatchers.Default) {
                    generatePlantLayout(width, height, highlightedCells)
                }
                
                if (newPlantCells.size >= densityTarget) {
                    plantCells.clear()
                    plantCells.addAll(newPlantCells)
                    foundLayout = true
                }
                delay(1)
            }
            
            isLoading = false
        }
    }

    if (triggerExtremeLayoutGen > 0) {
        LaunchedEffect(triggerExtremeLayoutGen) {
            isLoading = true
            attemptsCount = 0
            var bestLayout: List<Int> = emptyList()
            var maxPlants = -1

            while (attemptsCount < 500) {
                attemptsCount++
                val newPlantCells = withContext(Dispatchers.Default) {
                    generatePlantLayout(width, height, highlightedCells)
                }
                if (newPlantCells.size > maxPlants) {
                    maxPlants = newPlantCells.size
                    bestLayout = newPlantCells
                }
                delay(1)
            }
            
            plantCells.clear()
            plantCells.addAll(bestLayout)
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Grid") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Button(onClick = {
                densityRequirement = 0.5f
                triggerLayoutGen++
            }) {
                Text("Generate optimum layout")
            }

            Button(onClick = {
                triggerExtremeLayoutGen++
            }) {
                Text("Generate extremely optimum layout")
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(width),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(width * height) { index ->
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .drawBehind {
                                    val strokeWidth = 1.dp.toPx()
                                    
                                    // Default black border for all cells
                                    drawLine(Color.Black, Offset(0f, 0f), Offset(size.width, 0f), strokeWidth)
                                    drawLine(Color.Black, Offset(0f, 0f), Offset(0f, size.height), strokeWidth)
                                    drawLine(Color.Black, Offset(size.width, 0f), Offset(size.width, size.height), strokeWidth)
                                    drawLine(Color.Black, Offset(0f, size.height), Offset(size.width, size.height), strokeWidth)

                                    val row = index / width
                                    val col = index % width
                                    val isPerimeter = row == 0 || row == height - 1 || col == 0 || col == width - 1

                                    if (isPerimeter) {
                                        if (highlightedCells.contains(index)) {
                                            val highlightColor = Color.Yellow
                                            if (row == 0) { drawLine(highlightColor, Offset(0f, 0f), Offset(size.width, 0f), strokeWidth) }
                                            if (row == height - 1) { drawLine(highlightColor, Offset(0f, size.height), Offset(size.width, size.height), strokeWidth) }
                                            if (col == 0) { drawLine(highlightColor, Offset(0f, 0f), Offset(0f, size.height), strokeWidth) }
                                            if (col == width - 1) { drawLine(highlightColor, Offset(size.width, 0f), Offset(size.width, size.height), strokeWidth) }
                                        }
                                    }
                                }
                                .clickable {
                                    val row = index / width
                                    val col = index % width
                                    val isPerimeter = row == 0 || row == height - 1 || col == 0 || col == width - 1
                                    if (isPerimeter) {
                                        if (highlightedCells.contains(index)) {
                                            highlightedCells.remove(index)
                                        } else {
                                            highlightedCells.add(index)
                                        }
                                    }
                                }
                        ) {
                            if (plantCells.contains(index)) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val potWidth = size.width * 0.6f
                                    val potHeight = size.height * 0.4f
                                    val potX = (size.width - potWidth) / 2
                                    val potY = size.height - potHeight
                                    drawRect(color = Color(0xFF8B4513), topLeft = Offset(potX, potY), size = Size(potWidth, potHeight))

                                    val stemWidth = size.width * 0.1f
                                    val stemHeight = size.height * 0.5f
                                    val stemX = (size.width - stemWidth) / 2
                                    val stemY = potY - stemHeight
                                    drawRect(color = Color.Green, topLeft = Offset(stemX, stemY), size = Size(stemWidth, stemHeight))

                                    val leafSize = size.width * 0.2f
                                    drawRect(color = Color.Green, topLeft = Offset(stemX - leafSize, stemY + stemHeight * 0.2f), size = Size(leafSize, leafSize))
                                    drawRect(color = Color.Green, topLeft = Offset(stemX + stemWidth, stemY + stemHeight * 0.2f), size = Size(leafSize, leafSize))
                                    drawRect(color = Color.Green, topLeft = Offset(stemX - leafSize * 0.5f, stemY), size = Size(leafSize, leafSize))
                                    drawRect(color = Color.Green, topLeft = Offset(stemX + stemWidth * 0.5f, stemY), size = Size(leafSize, leafSize))
                                }
                            }
                        }
                    }
                }

                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(120.dp)) {
                            drawCircle(color = Color.Black.copy(alpha = 0.5f), radius = size.minDimension / 2)
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Attempt: $attemptsCount", color = Color.White)
                        }
                    }
                }
            }

            Text(
                text = "Total Plants: ${plantCells.size}",
                modifier = Modifier.padding(top = 10.dp)
            )
        }
    }
}

fun generatePlantLayout(width: Int, height: Int, highlightedCells: List<Int>): List<Int> {
    if (highlightedCells.isEmpty()) {
        return (0 until width * height).toList()
    }

    var bestLayout: List<Int> = emptyList()
    var maxPlants = -1

    for (exit in highlightedCells) {
        val currentLayout = generateLayoutForSingleExit(width, height, exit)
        if (currentLayout.size > maxPlants) {
            maxPlants = currentLayout.size
            bestLayout = currentLayout
        }
    }
    return bestLayout
}

fun generateLayoutForSingleExit(width: Int, height: Int, unblockedExit: Int): List<Int> {
    val plantCells = (0 until width * height).toMutableSet()
    val corridorCells = mutableSetOf(unblockedExit)
    plantCells.remove(unblockedExit)

    val processingQueue = ArrayDeque(plantCells.shuffled())

    while(processingQueue.isNotEmpty()){
        val plant = processingQueue.removeFirst()
        if(plant !in plantCells) continue

        val x = plant % width
        val y = plant / height
        val neighbors = mutableListOf<Int>()
        if (x > 0) neighbors.add(plant - 1)
        if (x < width - 1) neighbors.add(plant + 1)
        if (y > 0) neighbors.add(plant - width)
        if (y < height - 1) neighbors.add(plant + width)
        
        if(neighbors.any { it in corridorCells }) {
            continue
        }

        val path = findPathToNearestCorridor(plant, corridorCells, width, height)
        if(path.isNotEmpty()){
            plantCells.removeAll(path)
            corridorCells.addAll(path)
        }
    }

    return plantCells.toList()
}

fun findPathToNearestCorridor(start: Int, corridor: Set<Int>, width: Int, height: Int): List<Int> {
    val queue = ArrayDeque<List<Int>>()
    queue.add(listOf(start))
    val visited = mutableSetOf(start)

    while (queue.isNotEmpty()) {
        val path = queue.removeFirst()
        val current = path.last()

        val x = current % width
        val y = current / height
        val neighbors = mutableListOf<Int>()
        if (x > 0) neighbors.add(current - 1)
        if (x < width - 1) neighbors.add(current + 1)
        if (y > 0) neighbors.add(current - width)
        if (y < height - 1) neighbors.add(current + width)

        for (neighbor in neighbors) {
            if (neighbor in corridor) {
                return path
            }
            if (!visited.contains(neighbor)) {
                visited.add(neighbor)
                val newPath = path.toMutableList()
                newPath.add(neighbor)
                queue.add(newPath)
            }
        }
    }
    return emptyList()
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    GridGeneratorTheme {
        InputScreen(rememberNavController())
    }
}
