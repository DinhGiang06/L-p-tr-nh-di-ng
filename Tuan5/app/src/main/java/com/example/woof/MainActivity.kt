package com.example.woof
import androidx.compose.ui.unit.dp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.woof.data.Dog
import com.example.woof.data.dogs
import com.example.woof.ui.theme.WoofTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WoofTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    WoofApp()
                }
            }
        }
    }
}

@Composable
fun WoofApp() {

    Scaffold(
        topBar = {
            WoofTopAppBar()
        }
    ) { padding ->

        LazyColumn(
            contentPadding = padding
        ) {

            items(dogs) { dog ->

                DogItem(
                    dog = dog,
                    modifier = Modifier.padding(8.dp)
                )

            }
        }

    }
}

@Composable
fun DogItem(
    dog: Dog,
    modifier: Modifier = Modifier
) {

    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {

        Column {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                DogIcon(dog.imageResourceId)

                DogInformation(
                    dog.name,
                    dog.age,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = { expanded = !expanded }
                ) {

                    Icon(
                        imageVector =
                            if (expanded)
                                Icons.Filled.ExpandLess
                            else
                                Icons.Filled.ExpandMore,
                        contentDescription = null
                    )

                }

            }

            AnimatedVisibility(expanded) {

                DogHobby(
                    dog.hobbies,
                    modifier = Modifier.padding(16.dp)
                )

            }

        }

    }
}

@Composable
fun DogHobby(
    @StringRes dogHobby: Int,
    modifier: Modifier = Modifier
) {

    Column(modifier = modifier) {

        Text(
            text = stringResource(R.string.about),
            style = MaterialTheme.typography.labelSmall
        )

        Text(
            text = stringResource(dogHobby),
            style = MaterialTheme.typography.bodyLarge
        )

    }

}

@Composable
fun DogIcon(
    @DrawableRes dogIcon: Int,
    modifier: Modifier = Modifier
) {

    Image(
        modifier = modifier
            .size(72.dp)
            .padding(8.dp)
            .clip(MaterialTheme.shapes.small),
        contentScale = ContentScale.Crop,
        painter = painterResource(dogIcon),
        contentDescription = null
    )

}

@Composable
fun DogInformation(
    @StringRes dogName: Int,
    dogAge: Int,
    modifier: Modifier = Modifier
) {

    Column(modifier = modifier) {

        Text(
            text = stringResource(dogName),
            style = MaterialTheme.typography.displayMedium
        )

        Text(
            text = stringResource(R.string.years_old, dogAge),
            style = MaterialTheme.typography.bodyLarge
        )

    }

}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WoofTopAppBar() {

    CenterAlignedTopAppBar(

        title = {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    painter = painterResource(R.drawable.ic_woof_logo),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )

                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.displayLarge
                )

            }

        }

    )

}