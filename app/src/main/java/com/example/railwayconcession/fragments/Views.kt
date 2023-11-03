package com.example.railwayconcession.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
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
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.example.railwayconcession.firebaseConfig
import com.example.railwayconcession.model.userConccessionDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

// here need to add recyclee view to show all applied one
class Views : Fragment() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            // Dispose of the Composition when the view's LifecycleOwner
            // is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    // In Compose world
                    concessionApplicationScreen()

                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun concessionApplicationScreen() {
    var list by remember { mutableStateOf(emptyList<userConccessionDetails>()) }

    DisposableEffect(firebaseConfig.rootReference) {
//        loading = true
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newMessages =
                    snapshot.children.mapNotNull { it.getValue(userConccessionDetails::class.java) }
                Log.d("DataFetch", "Fetched messages: $newMessages")
                list = newMessages
//                loading = false
            }

            override fun onCancelled(error: DatabaseError) {
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
                    ConcessionListItem(item)
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No History Found")
            }
        }
    }


}

@Composable
fun ConcessionListItem(
    item: userConccessionDetails,
//    source: String,
//    destination: String,
//    classs: String,
//    duration: String,
//    voucherNo: String,
//    appliedDate: String
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
//        elevation = 4.dp
    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(2.dp)
//                .weight(1f)
//        ) {
//            Row(
//                horizontalArrangement = Arrangement.Start, modifier = Modifier
//                    .weight(1f)
//            ) {
//                Text(text = "Source : ")
//                Text(text = item.source.toString(), fontWeight = FontWeight.Bold)
//            }
//            Row(
//                horizontalArrangement = Arrangement.Start, modifier = Modifier
//                    .weight(1f)
//            ) {
//                Text(text = "Class : ")
//                Text(text = "class ")
//            }
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .wrapContentHeight()
//            ) {
//                Text(text = "Voucher Number : ")
//                Text(text = item.voucherNo.toString(), fontWeight = FontWeight.Bold)
//            }
//        }
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(2.dp)
//                .weight(1f)
//        ) {
//            Row(
//                horizontalArrangement = Arrangement.Start
//            ) {
//                Text(text = "Destination : ")
//                Text(text = item.destination.toString(), fontWeight = FontWeight.Bold)
//            }
//            Row(
//                horizontalArrangement = Arrangement.Start, modifier = Modifier
//                    .weight(1f)
//            ) {
//                Text(text = "Duration : ")
//                Text(text = item.concessionPeriod.toString(), fontWeight = FontWeight.Bold)
//            }
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .wrapContentHeight()
//            ) {
//                Text(text = "Application Date : ")
//                Text(text = item.appliedDate.toString(), fontWeight = FontWeight.Bold)
//            }
//        }
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
                    Text(text = item.concession_class.toString(), fontWeight = FontWeight.Bold)
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
        }


        //gpt
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(12.dp)
//                ) {
//                    Row(
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                        modifier = Modifier.fillMaxWidth()
//                    ) {
//                        Column {
//                            Text(text = "Source:", fontWeight = FontWeight.Bold)
//                            Text(text = item.source.toString())
//                        }
//                        Column {
//                            Text(text = "Destination:", fontWeight = FontWeight.Bold)
//                            Text(text = item.destination.toString())
//                        }
//                    }
//
//                    Spacer(modifier = Modifier.height(8.dp))
//
//                    Row(
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                        modifier = Modifier.fillMaxWidth()
//                    ) {
//                        Column {
//                            Text(text = "Class:", fontWeight = FontWeight.Bold)
//                            Text(text = item.concession_class.toString())
//                        }
//                        Column {
//                            Text(text = "Duration:", fontWeight = FontWeight.Bold)
//                            Text(text = item.concessionPeriod.toString())
//                        }
//                    }
//
//                    Spacer(modifier = Modifier.height(8.dp))
//
//                    Row(
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                        modifier = Modifier.fillMaxWidth()
//                    ) {
//                            Text(text = "Voucher Number:", fontWeight = FontWeight.Bold)
//                            Text(text = item.voucherNo.toString())
//                    }
//
//                    Spacer(modifier = Modifier.height(8.dp))
//
//                    Text(
//                        text = "Application Date: ${item.appliedDate}",
//                        fontWeight = FontWeight.Bold
//                    )
//                }

    }
}