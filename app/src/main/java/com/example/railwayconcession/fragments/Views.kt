package com.example.railwayconcession.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Snackbar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.railwayconcession.R
import com.example.railwayconcession.activities.concession_form
import com.example.railwayconcession.activities.login
import com.example.railwayconcession.firebaseConfig
import com.example.railwayconcession.model.userConccessionDetails
import com.google.firebase.annotations.concurrent.Background
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import java.util.Locale

// here need to add recyclee view to show all applied one
class Views : Fragment() {

    public lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        navController = findNavController()

        return ComposeView(requireContext()).apply {
            // Dispose of the Composition when the view's LifecycleOwner
            // is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    // In Compose world
                    concessionApplicationScreen(navController)

                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun concessionApplicationScreen(navController: NavController) {
    var list by remember { mutableStateOf(emptyList<userConccessionDetails>()) }
    var showProgressBar by remember { mutableStateOf(true) } // New state for progress bar

    if (showProgressBar) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator() // Circular indeterminate progress indicator
        }
    }

//    showProgressBar = true


DisposableEffect(firebaseConfig.rootReference) {
        showProgressBar = true
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
//                showProgressBar = false

                val newMessages =
                    snapshot.children.mapNotNull { it.getValue(userConccessionDetails::class.java) }
                Log.d("DataFetch", "Fetched messages: $newMessages")
                list = newMessages
                showProgressBar = false

//                loading = false
            }

            override fun onCancelled(error: DatabaseError) {
                showProgressBar = false

                Log.e("DataFetch", "Data fetch error: $error")
//                loading = false
            }
        }

        var auth: FirebaseAuth = Firebase.auth
        val email = auth.currentUser?.email
        val clgId = email?.substring(0, 11)?.toUpperCase()
        firebaseConfig.userRef.child("$clgId/CLIST").addValueEventListener(listener)
//        database.child(studentClassName).child("ALL").addValueEventListener(listener)

        onDispose {
            firebaseConfig.userRef.child("VU1F2122010").removeEventListener(listener)
            Log.d("DataFetch", "Listener removed")
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Your Status", modifier = Modifier.padding(10.dp))
        Divider()
        if (list.isNotEmpty()) {
            LazyColumn {
                items(list) { item ->
//                    showProgressBar = false
                    ConcessionListItem(item,navController)

                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
//                showProgressBar = false
                Text(text = "No History Found")

            }
        }
    }


}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ConcessionListItem(
    item: userConccessionDetails,
//    source: String,
//    destination: String,
//    classs: String,
//    duration: String,
//    voucherNo: String,
//    appliedDate: String
    navController: NavController
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showSnackbar by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
//        elevation = 4.dp
    ) {

//        showProgressBar = true
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start, modifier = Modifier
                        .weight(1f)
                ) {
                    Text(text = "Source : ")
                    Text(text = item.source.toString(), fontWeight = FontWeight.Bold)
//                    showProgressBar = false

                }
//                Spacer(modifier = Modifier.weight(1f))
                Row(
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(text = "Destination : ")
                    Text(text = item.destination.toString(), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start, modifier = Modifier
                        .weight(1f)
                ) {
                    Text(text = "Class : ")
                    Text(text = item.concessionClass.toString(), fontWeight = FontWeight.Bold)
                }
//                Spacer(modifier = Modifier.weight(1f))
                Row(
                    horizontalArrangement = Arrangement.End, modifier = Modifier
                        .weight(1f)
                ) {
                    Text(text = "Duration : ")
                    Text(text = item.concessionPeriod.toString(), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Text(text = "Voucher Number : ")
                Text(text = item.voucherNo.toString(), fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Text(text = "Application Date : ")
                Text(text = item.appliedDate.toString(), fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start, modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            navController.navigate(R.id.action_views_to_update)
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(Color.Blue)
                    ) {
                        Text(text = "Update")

                    }
                }
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .weight(1f),
                )
                {
                    Button(
                        onClick = {
                            showDeleteDialog = true
//                        showSnackbar = true
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(Color.Red)

                    ) {
                        Text(text = "Delete")
                    }
                }
            }
        }

        if (showSnackbar) {
            Snackbar(
                modifier = Modifier.padding(16.dp),
                content = {
                    Text("Item deleted")
                }
            )
        }


        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = {
                    Text(text = "Delete Item")
                },
                text = {
                    Text(text = "Are you sure you want to delete this item?")
                },
                confirmButton = {
                    TextButton(
                        onClick = {

                            var auth: FirebaseAuth = Firebase.auth
                            val email = auth.currentUser?.email
                            val clgId = email?.substring(0, 11)?.uppercase(Locale.ROOT)

                            val timeForRetrieve = ArrayList<String>()

                            firebaseConfig.userRef.child("$clgId/CLIST")
                                .addValueEventListener(object : ValueEventListener {
                                    @RequiresApi(Build.VERSION_CODES.O)
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if (snapshot.exists()) {
                                            for (snapshot in snapshot.children) {
                                                val key = snapshot.key
                                                if (key != null) {
                                                    timeForRetrieve.add(key)
//                            Toast.makeText(this@concession_form, key, Toast.LENGTH_SHORT).show()
                                                }
                                            }

                                            // Get the last element from the list
                                            val lastTime = timeForRetrieve.lastOrNull()

                                            if (lastTime != null) {
                                                firebaseConfig.userRef.child("$clgId/CLIST")
                                                    .addValueEventListener(object :
                                                        ValueEventListener {
                                                        override fun onDataChange(snapshot: DataSnapshot) {
                                                            if (snapshot.exists()) {
                                                                firebaseConfig.userRef.child("$clgId/CLIST").child("$lastTime").removeValue()
                                                                    .addOnSuccessListener {

                                                                    }
                                                                    .addOnFailureListener {

                                                                    }
                                                            }
                                                        }

                                                        override fun onCancelled(error: DatabaseError) {
                                                            // Handle the error
                                                        }
                                                    })
                                            }
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
//                                    Toast.makeText(requireContext(), "Cannot get key", Toast.LENGTH_SHORT).show()
                                    }
                                })

                            showDeleteDialog = false
                        }
                    ) {
                        Text(text = "Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text(text = "No")

                    }
                }
            )
        }
//        if (showProgressBar) {
//            Box(
//                modifier = Modifier.fillMaxSize(),
//                contentAlignment = Alignment.Center
//            ) {
//                CircularProgressIndicator() // Circular indeterminate progress indicator
//            }
//        }
//    }


//        if (showSnackbar.value) {
//            Snackbar(
//                modifier = Modifier.padding(16.dp),
//                snackbarData = { state ->
//                    Text(text = "This is a Snackbar message")
//                    // You can also add an action button
//                    Button(
//                        onClick = {
//                            // Handle the action click here
//                            showSnackbar.value = false
//                        }
//                    ) {
//                        Text("Action")
//                    }
//                },
//                onDismiss = {
//                    showSnackbar.value = false
//                }
//            )
//        }
//    }
    }
}