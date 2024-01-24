package hashtag.generator.hashapplication.Saved

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import co.lujun.androidtagview.TagContainerLayout
import co.lujun.androidtagview.Tagmodel
import com.project.hashapplication.R
import com.project.hashapplication.databinding.FragmentSaveBinding
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SaveFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SaveFragment : Fragment(), savedFilesInterface {
    lateinit var binding: FragmentSaveBinding
    var selected_list=ArrayList<Tagmodel>()

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    lateinit var list_of_file_data: ArrayList<FileData>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSaveBinding.inflate(inflater, container, false)

        getDataFromFiles()

        if(list_of_file_data.size<1){
            binding.noSavedItem.visibility=View.VISIBLE
        }
        binding.recyclerViewSaved.layoutManager=LinearLayoutManager(requireContext())
        binding.recyclerViewSaved.adapter= SavedFilesAdaptor(requireContext(),list_of_file_data,this)


        binding.cardSelectedTagsSaved.setOnClickListener(object :View.OnClickListener{
            override fun onClick(p0: View?) {
                showSelectedTagsDialog(requireContext())
            }

        })
        // Inflate the layout for this fragment
        return binding.root
    }

    @SuppressLint("SuspiciousIndentation")
    fun showSelectedTagsDialog(activity: Context) {
        val dialog = Dialog(activity)
        dialog.setContentView(R.layout.dialogue_selected_tags)
        dialog.window!!.attributes.gravity = Gravity.CENTER;
        dialog.window!!.setBackgroundDrawableResource(com.google.android.material.R.color.mtrl_btn_transparent_bg_color)

        dialog.findViewById<CardView>(R.id.card_save).visibility=View.GONE
        dialog.findViewById<CardView>(R.id.card_delete).visibility=View.GONE
//        dialog.setCancelable(true)
        var dialog_tags_id = dialog.findViewById<TagContainerLayout>(R.id.tag_group)
        for (i in selected_list) {
            dialog_tags_id.isEnableCross=false
            dialog_tags_id.addTag(i.mname,i.mbol)
        }
        dialog_tags_id.setOnTagClickListener(object :
            co.lujun.androidtagview.TagView.OnTagClickListener {
            override fun onTagClick(position: Int, text: String?) {
            }

            override fun onTagLongClick(position: Int, text: String?) {
            }

            override fun onSelectedTagDrag(position: Int, text: String?) {
            }

            override fun onTagCrossClick(position: Int) {
            }

            override fun onTagstarClick(position: Int) {
            }

        })

        var close=dialog.findViewById<ImageView>(R.id.close_dialog_id)
        close.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                dialog.dismiss()
            }
        })
        val dialog_card_copy = dialog.findViewById<CardView>(R.id.card_copy)
        dialog_card_copy.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                var joinned_string = ""
                for (i in 0 until dialog_tags_id.tags.size) {
                    joinned_string += dialog_tags_id.tags.get(i).mname.toString() + " "
                }
                copyTags(joinned_string)
            }

        })
        dialog.show()
    }

    fun copyTags(joinned_string: String) {
        if (!joinned_string.isBlank()) {
            val clipboard =
                requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip =
                ClipData.newPlainText("label_quote", joinned_string.toString())
            Toast.makeText(requireContext(), "Copied to clipboard ", Toast.LENGTH_SHORT)
                .show()
            clipboard.setPrimaryClip(clip)

            var intent: Intent = Intent(Intent.ACTION_SEND)
            intent.setType("text/plain")
            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Copied Text");
            intent.putExtra(android.content.Intent.EXTRA_TEXT, joinned_string);
            requireContext().startActivity(Intent.createChooser(intent, "Share"))
        } else {
            Toast.makeText(requireContext(), "Can not copy blank text ", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun getDataFromFiles() {
        var text: StringBuilder = StringBuilder()
        list_of_file_data = ArrayList<FileData>()
        val file = File(requireContext().getExternalFilesDir("Saved Data"), "")
        var fileNameList = file.listFiles();
        if (fileNameList.size != 0) {
            for (i in fileNameList.indices) {
                text = StringBuilder()
                var nameOfFile:String=""
                nameOfFile=fileNameList.get(i).name
                try {
                    val br = BufferedReader(FileReader(fileNameList.get(i)))
                    var line: String?
                    while (br.readLine().also { line = it } != null) {
                        text.append(line)
                    }

                    Log.d("TAG text", "onCreateView: " + text.toString())
                    list_of_file_data.add(FileData(text.toString(),nameOfFile.substring(0,nameOfFile.length-4),fileNameList.get(i).path))
                    br.close()
                } catch (e: IOException) {
                }
            }
        }
    }

    class FileData(var fileData:String,var filename:String,var uri:String)

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            SaveFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }

    override fun show_card_Selected(list_of_file: ArrayList<Tagmodel>) {
        if(!list_of_file.size.equals(0)){
            binding.cardSelectedTagsSaved.visibility=View.VISIBLE
            binding.textSelectedTagsSaved.text=list_of_file.size.toString()
            selected_list=list_of_file
        }
        else{
            binding.cardSelectedTagsSaved.visibility=View.GONE
        }
    }

    override fun showNoItem(b: Boolean) {
        if(b){
            binding.noSavedItem.visibility=View.VISIBLE
        }
    }
}