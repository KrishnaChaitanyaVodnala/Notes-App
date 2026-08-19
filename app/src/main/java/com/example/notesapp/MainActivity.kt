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
import com.example.notesapp.ui.theme.NotesAppTheme
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Switch
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.notesapp.data.Note
import com.example.notesapp.data.NotesDatabase
import com.example.notesapp.repository.NotesRepository
import com.example.notesapp.repository.NotesRepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import androidx.compose.material3.SwipeToDismissBoxValue.StartToEnd
import androidx.compose.material3.SwipeToDismissBoxValue.EndToStart
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.painterResource

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val repository = NotesRepositoryImpl(NotesDatabase.getDatabase(applicationContext).notesDao())
            val notesViewModel: NotesViewModel = viewModel(factory = NotesViewModelFactory(repository))
            NotesAppRoot(notesViewModel)
        }
    }
}

@Composable
fun NotesAppRoot(notesViewModel: NotesViewModel) {

    val systemDarkTheme = isSystemInDarkTheme()

    var darkTheme by rememberSaveable { mutableStateOf(systemDarkTheme) }

    NotesAppTheme(darkTheme = darkTheme) {

        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Surface(tonalElevation = 5.dp) {
                NotesApp(
                    notesViewModel = notesViewModel,
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
    notesViewModel: NotesViewModel,
    modifier:Modifier = Modifier,
    darkTheme: Boolean = false,
    onThemeChange: () -> Unit = { }
) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Notes.route) {
        composable(route = Screen.Notes.route) {
            NotesScreen(
                modifier = modifier,
                notesViewModel = notesViewModel,
                darkTheme = darkTheme,
                onThemeChange = onThemeChange,
                onCreateNote = {
                    navController.navigate(Screen.Create.route)
                },
                displayNote = {
                    navController.navigate(Screen.Display.withArgs(it.toString()))
                }
            )
        }

        composable(route = Screen.Create.route) {
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
            route = Screen.Display.route + "/{id}",
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

            val note = notesViewModel.searchNote(id)

            DisplayNote(
                modifier = modifier,
                note = note,
                row = false,
                onSave = { notesViewModel.updateNote(note, it)}
            )
        }
    }

}

@Composable
fun NotesScreen(
    modifier: Modifier = Modifier,
    notesViewModel: NotesViewModel,
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
            Spacer(modifier = Modifier.height(8.dp))
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
    note: Note?,
    row: Boolean = true,
    onClick: (Int) -> Unit = { },
    onDelete: (Note) -> Unit = { },
    onSave: (List<String>) -> Boolean = { true }
) {
    if(note == null) return

    if(row) {

        val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
            confirmValueChange = {
                if(it == StartToEnd) onClick(note.id)
                else if(it == EndToStart) onDelete(note)
                // Reset item when toggling done status
                it != StartToEnd
            }
        )

        SwipeToDismissBox(
            state = swipeToDismissBoxState,
            modifier = modifier.fillMaxSize(),
            backgroundContent = {
                when(swipeToDismissBoxState.dismissDirection) {
                    StartToEnd -> {
                        Icon(
                            painter = painterResource(R.drawable.update_icon),
                            contentDescription = "Update Note",
                            modifier = Modifier
                                .fillMaxSize()
                                .drawBehind {
                                    drawRect(lerp(Color.LightGray, Color.Blue, swipeToDismissBoxState.progress))
                                }
                                .wrapContentSize(Alignment.CenterStart)
                                .padding(12.dp),
                            tint = Color.White
                        )
                    }

                    EndToStart -> {
                        Icon(
                            painter = painterResource(R.drawable.delete_icon),
                            contentDescription = "Remove Note",
                            modifier = Modifier
                                .fillMaxSize()
                                .background(lerp(Color.LightGray, Color.Red, swipeToDismissBoxState.progress))
                                .wrapContentSize(Alignment.CenterStart)
                                .padding(12.dp),
                            tint = Color.White
                        )
                    }

                    SwipeToDismissBoxValue.Settled -> {}
                }
            }
        ) {

            Row(
                modifier = Modifier
                    .clickable {
                        onClick(note.id)
                    }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = note.title,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
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

            var title by rememberSaveable { mutableStateOf(note.title) }
            var notes by rememberSaveable { mutableStateOf(note.notes) }
            var isInputValid by rememberSaveable { mutableStateOf(true) }
            var isEditing by rememberSaveable { mutableStateOf(false) }

            TextField (
                value = title,
                onValueChange = {
                    isEditing = true
                    title = it
                    isInputValid = true
                },
                textStyle = TextStyle(
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 20.sp,
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .border(
                        2.dp,
                        MaterialTheme.colorScheme.outline,
                        RoundedCornerShape(8.dp)
                    )
                    .fillMaxWidth()
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
            )

            val scrollState = rememberScrollState()

            TextField(
                value = notes,
                onValueChange = {
                    isEditing = true
                    notes = it
                },
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp,
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                    .fillMaxWidth()
                    .weight(1f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            var isLoading by rememberSaveable { mutableStateOf(false) }

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
                    isLoading = true
                    isInputValid = onSave(listOf(title, notes))
                    isLoading = false
                    isEditing = false
                }
            ) {
                Text(if(isLoading) "Saving.." else "Save Changes")
            }
            if (!isInputValid) {
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    "Title cannot be Empty!",
                    color = MaterialTheme.colorScheme.error
                )
            }
            Spacer(modifier = Modifier.height(5.dp))

            if(isEditing) {
                Text(
                    "Changes not saved ❗",
                    color = MaterialTheme.colorScheme.tertiary
                )
            } else {
                Text(
                    "All changes are up to date ✔\uFE0F",
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
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

    val fakeRepository = object : NotesRepository {
        override val notes: Flow<List<Note>> = flowOf(
            listOf(Note(id = 1, title = "Sample", notes = "Preview note"))
        )

        override suspend fun addNote(note: Note) {
            TODO("Not yet implemented")
        }

        override suspend fun deleteNote(note: Note) {
            TODO("Not yet implemented")
        }

        override suspend fun updateNote(note: Note) {
            TODO("Not yet implemented")
        }
    }
    val previewViewModel = NotesViewModel(fakeRepository)
    NotesAppTheme {
        NotesApp(previewViewModel)
    }
}