package com.example.biomecardapp2.ui.collections

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.biomecardapp2.R
import com.example.biomecardapp2.databinding.FragmentCollectionsBinding
import com.example.biomecardapp2.databinding.ItemViewBinding
import kotlinx.coroutines.*
import java.net.HttpURLConnection
import java.net.URL

class CollectionsFragment : Fragment() {

    private var _binding: FragmentCollectionsBinding? = null
    private lateinit var _binding2: ItemViewBinding
    private val binding get() = _binding!!
    private lateinit var adapter: MyAdapter
    private var imageJob: Job? = null
    var bitmap: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding2 = ItemViewBinding.inflate(layoutInflater)
        _binding = FragmentCollectionsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val layoutManager = LinearLayoutManager(this.requireContext())
        binding.myRecyclerview.setLayoutManager(layoutManager)

        val divider = DividerItemDecoration(
            this.requireContext(), layoutManager.orientation
        )
        binding.myRecyclerview.addItemDecoration(divider)


        adapter = MyAdapter()
        binding.myRecyclerview.setAdapter(adapter)
        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class MyViewHolder(private val itemView: View) :
        RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        init {

        }

        fun setText(text: String, pictureUrl: String) {
            itemView.findViewById<TextView>(R.id.textview_cardtitle)
                .setText(text)
            imageJob = CoroutineScope(Dispatchers.IO).launch {
                val currentImageUrl = pictureUrl

                val url = URL(currentImageUrl)
                val connection: HttpURLConnection =
                    url.openConnection() as HttpURLConnection


                try {
                    connection.getInputStream().use { stream ->
                        bitmap = BitmapFactory.decodeStream(stream)
                    }
                } finally {
                    connection.disconnect()

                }

            }
            while (imageJob!!.isActive) {
            }
            Log.i("bitmap", "bitmap = ${bitmap}")
            itemView.findViewById<ImageView>(R.id.imageView)
                .setImageBitmap(bitmap)

        }


        override fun onClick(view: View?) {

        }

    }

    inner class MyAdapter() : RecyclerView.Adapter<MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.setText("Card No. ${position}", imageURLs.getList().get(position))
            Log.i("bitmap", "url = ${imageURLs.getList().get(position)}")

        }

        override fun getItemCount(): Int {
            return imageURLs.getList().size
        }
    }
}