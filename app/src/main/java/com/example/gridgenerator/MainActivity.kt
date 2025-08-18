package com.example.gridgenerator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
    NavHost(navController = navController, startDestination = "input") {
        composable("input") {
            InputScreen(navController)
        }
        composable("grid/{width}/{height}") { backStackEntry ->
            val width = backStackEntry.arguments?.getString("width")?.toIntOrNull() ?: 1
            val height = backStackEntry.arguments?.getString("height")?.toIntOrNull() ?: 1
            GridScreen(width = width, height = height)
        }
    }
}

@Composable
fun InputScreen(navController: NavController) {
    var width by remember { mutableStateOf(TextFieldValue("10")) }
    var height by remember { mutableStateOf(TextFieldValue("10")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
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

@Composable
fun GridScreen(width: Int, height: Int) {
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

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
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

    while (true) {
        val invalidPlants = plantCells.filter { plant ->
            val x = plant % width
            val y = plant / width
            val neighbors = mutableListOf<Int>()
            if (x > 0) neighbors.add(plant - 1)
            if (x < width - 1) neighbors.add(plant + 1)
            if (y > 0) neighbors.add(plant - width)
            if (y < height - 1) neighbors.add(plant + width)
            neighbors.none { it in corridorCells }
        }

        if (invalidPlants.isEmpty()) {
            break
        }

        val plantToConnect = invalidPlants.random()
        
        val path = findPathToNearestCorridor(plantToConnect, corridorCells, width, height)
        
        if (path.isNotEmpty()) {
            plantCells.removeAll(path)
            corridorCells.addAll(path)
        } else {
            plantCells.remove(plantToConnect)
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
        val y = current / width
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