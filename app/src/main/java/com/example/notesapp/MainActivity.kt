package com.example.notesapp

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notesapp.repository.NotesRepository
import com.example.notesapp.ui.theme.NotesAppTheme
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotesAppRoot()
        }
    }
}

@Composable
fun NotesAppRoot() {

    val systemDarkTheme = isSystemInDarkTheme()

    var darkTheme by rememberSaveable { mutableStateOf(systemDarkTheme) }

    NotesAppTheme(darkTheme = darkTheme) {

        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Surface(tonalElevation = 5.dp) {
                NotesApp(
                    modifier = Modifier.padding(innerPadding),
                    darkTheme = darkTheme,
                    onThemeChange = {
                        darkTheme = !darkTheme
                    }
                )
            }
        }
    }
}

@Composable
fun NotesApp(
    modifier:Modifier = Modifier,
    darkTheme: Boolean = false,
    onThemeChange: () -> Unit = { }
) {

    val navController = rememberNavController()

    val notesViewModel: NotesViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screen.notes.route) {
        composable(route = Screen.notes.route) {
            NotesScreen(
                modifier = modifier,
                notesViewModel = notesViewModel,
                darkTheme = darkTheme,
                onThemeChange = onThemeChange,
                onCreateNote = {
                    navController.navigate(Screen.create.route)
                },
                displayNote = {
                    navController.navigate(Screen.display.withArgs(it.toString()))
                }
            )
        }

        composable(route = Screen.create.route) {
            OnCreate(
                modifier = modifier,
                onAdd = {
                    notesViewModel.addNote(it)
                },
                onEntry = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.display.route + "/{id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.IntType
                }
            )
        ) { entry ->

            val id = entry.arguments?.getInt("id")

            if(id == null) {
                navController.popBackStack()
                return@composable
            }

            DisplayNote(
                modifier = modifier,
                note = notesViewModel.searchNote(id),
                row = false
            )
        }
    }

}

@Composable
fun NotesScreen(
    modifier: Modifier = Modifier,
    notesViewModel: NotesViewModel = viewModel(),
    darkTheme: Boolean = false,
    onThemeChange: () -> Unit = { },
    onCreateNote: () -> Unit,
    displayNote: (Int) -> Unit
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        val notes by notesViewModel.notes.collectAsState()

        Column(
            modifier = Modifier
                .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Notes",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.Center)
                )
                Switch(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    checked = darkTheme,
                    onCheckedChange = { onThemeChange() }
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
            )
            if(notes.isEmpty()) {
                Box (
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No notes yet", color = MaterialTheme.colorScheme.onSurface)
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(notes) { note ->
                        DisplayNote(
                            note = note,
                            onClick = {
                                displayNote(it)
                            },
                            onDelete =  { notesViewModel.deleteNote(it) }
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
            )

            ElevatedButton(
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                elevation = ButtonDefaults.elevatedButtonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 8.dp
                ),
                onClick = onCreateNote
            ) {
                Text("Create Note")
            }
        }
    }

}

@Composable
fun OnCreate(
    modifier: Modifier = Modifier,
    onAdd: (List<String>) -> Boolean,
    onEntry: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        var isInputValid by rememberSaveable { mutableStateOf(true) }
        var title by rememberSaveable { mutableStateOf("") }
        OutlinedTextField(
            value = title,
            modifier = Modifier
                .padding(10.dp)
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxWidth(),
            onValueChange = {
                title = it
                isInputValid = true
            },
            placeholder = {
                Text("Enter Title")
            }
        )
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant,
        )
        var notes by rememberSaveable { mutableStateOf("") }
        OutlinedTextField(
            value = notes,
            onValueChange = {
                notes = it
            },
            modifier = Modifier
                .padding(10.dp)
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxWidth()
                .weight(1f),
            placeholder = {
                Text("Write your notes..")
            }
        )
        Spacer(modifier = Modifier.height(8.dp))

        ElevatedButton(
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            elevation = ButtonDefaults.elevatedButtonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp
            ),
            onClick = {
                isInputValid = onAdd(listOf(title, notes))

                if (isInputValid) onEntry()
            }
        ) {
            Text("Create Note")
        }
        if (!isInputValid) {
            Text(
                "Title cannot be Empty!",
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun DisplayNote(
    modifier: Modifier = Modifier,
    note: NotesRepository.Note?,
    row: Boolean = true,
    onClick: (Int) -> Unit = { },
    onDelete: (Int) -> Unit = { }
) {
    if(note == null) return

    if(row) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = note.title,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .clickable {
                        onClick(note.id)
                    }
                    .weight(1f),
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    onDelete(note.id)
                }
            ) {
                Text("-")
            }
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = note.title,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                    .fillMaxWidth()
                    .padding(10.dp),
                fontSize = 20.sp
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
            )

            val scrollState = rememberScrollState()

            Text(
                note.notes,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(10.dp),
                fontSize = 18.sp
            )
        }
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "DefaultPreviewLight"
)
@Composable
fun DefaultPreview() {
    NotesAppTheme {
        NotesApp()
    }
}