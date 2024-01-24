package hashtag.generator.hashapplication.Favourites

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.cardview.widget.CardView
import co.lujun.androidtagview.TagContainerLayout
import co.lujun.androidtagview.TagView
import co.lujun.androidtagview.Tagmodel
import com.project.hashapplication.R
import com.project.hashapplication.databinding.FragmentFavouriteBinding
import io.paperdb.Paper
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"

/**
 * A simple [Fragment] subclass.
 * Use the [FavouriteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavouriteFragment : Fragment() {
    lateinit var binding: FragmentFavouriteBinding
    var selected_list = ArrayList<String>()
    var selected_tags_list = ArrayList<Tagmodel>()
    var all_tags_total_selected: Int = 0
    lateinit var fav_List: ArrayList<String>

    // TODO: Rename and change types of parameters
    private var param1: String? = null

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
        binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        fav_List = Paper.book().read("fav_tags", ArrayList<String>())!!

        if (fav_List!!.size == 0) {
            setVisibilities()

        } else {
            for (i in 0 until fav_List!!.size) {
                binding.tagGroup.addTag(fav_List.get(i),true)
            }
        }


        binding.tagGroup.setOnTagClickListener(object : TagView.OnTagClickListener {
            override fun onTagClick(position: Int, text: String?) {
            }

            override fun onTagLongClick(position: Int, text: String?) {
                binding.checkBox.isChecked = false
                if (binding.tagGroup.toggleSelectTagView(position, false)) {
                    all_tags_total_selected += 1
                    binding.cardSelectedTagsFavourites.visibility = View.VISIBLE
                    binding.textSelectedTagsFavourites.text = all_tags_total_selected.toString()
                    selected_tags_list += binding.tagGroup.tags.get(position)
                } else {
                    all_tags_total_selected -= 1
                    binding.textSelectedTagsFavourites.text = all_tags_total_selected.toString()
                    if (binding.textSelectedTagsFavourites.text.equals("0")) {
                        setVisibilities()
                    }
                    selected_tags_list.remove(binding.tagGroup.tags.get(position))
                    binding.tagGroup.toggleSelectTagView(position, true)
                }
            }

            override fun onSelectedTagDrag(position: Int, text: String?) {

            }

            override fun onTagCrossClick(position: Int) {
                removeTag(position)
            }

            override fun onTagstarClick(position: Int) {
            }
        })


        binding.cardCopy.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                var joinned_string = ""
                for (i in 0 until binding.tagGroup.tags.size) {
                    joinned_string += binding.tagGroup.tags.get(i).mname.toString() + " "
                }
                copyTags(joinned_string)
            }

        })

        binding.cardSave.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                var tags_in_group = ""
                var i = 0
                while (i < binding.tagGroup.tags.size) {
                    tags_in_group += binding.tagGroup.tags.get(i).mname.toString()
                    i++
                }
                saveTags(tags_in_group, "")
            }

        })

        binding.checkBox.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (binding.checkBox.isChecked) {
                    var i = 0
                    while (i < binding.tagGroup.tags.size) {
                        binding.tagGroup.toggleSelectTagView(i, true)
                        i++
                    }
                    all_tags_total_selected = 0
                    all_tags_total_selected += binding.tagGroup.tags.size
                    binding.cardSelectedTagsFavourites.visibility = View.VISIBLE
                    binding.textSelectedTagsFavourites.text = all_tags_total_selected.toString()
                    selected_tags_list += binding.tagGroup.tags
                } else {
                    var i = 0
                    while (i < binding.tagGroup.tags.size) {
                        binding.tagGroup.toggleSelectTagView(i, false)
                        i++
                    }
                    all_tags_total_selected -= binding.tagGroup.tags.size
                    binding.textSelectedTagsFavourites.text = all_tags_total_selected.toString()
                    if (binding.textSelectedTagsFavourites.text.equals("1") || binding.textSelectedTagsFavourites.text.equals(
                            "0"
                        )
                    ) {
                        binding.textSelectedTagsFavourites.text = "1"
                        binding.cardSelectedTagsFavourites.visibility = View.GONE
                        selected_tags_list = ArrayList<Tagmodel>()

                        var i = 0
                        while (i < binding.tagGroup.tags.size) {
                            binding.tagGroup.toggleSelectTagView(i, true)
                            i++
                        }
                    }
                }
            }

        })


        binding.cardSelectedTagsFavourites.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                showSelectedTagsDialog(requireContext())
            }
        })
        return binding.root
    }

    fun setVisibilities() {
        binding.nofavouriteItem.visibility = View.VISIBLE
        binding.relativeCopyAndSave.visibility = View.GONE
        binding.relativeCheckbox.visibility = View.GONE
    }

    fun saveTags(sFileName: String, title: String) {
        val directory = File(requireContext().getExternalFilesDir("Saved Data"), "")
        if (!directory.exists()) {
            directory.mkdir()
        }
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US)
        val now = Date()
        val fileName: String = formatter.format(now)
        var gpxfile: File? = null
        try {
            if (title.equals("")) {
                gpxfile = File(directory, fileName + ".txt")
            } else {
                gpxfile = File(directory, title + ".txt")
            }
            val writer = FileWriter(gpxfile)
            writer.append(
                sFileName.substring(1)
            )
            writer.flush()
            writer.close()
            Toast.makeText(context, "File have been saved", Toast.LENGTH_SHORT).show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun showSelectedTagsDialog(activity: Context) {
        val dialog = Dialog(activity)
        dialog.setContentView(R.layout.dialogue_selected_tags)
        dialog.window!!.attributes.gravity = Gravity.CENTER;
        dialog.window!!.setBackgroundDrawableResource(com.google.android.material.R.color.mtrl_btn_transparent_bg_color)

        dialog.findViewById<CardView>(R.id.card_save).visibility = View.GONE
        dialog.findViewById<CardView>(R.id.card_delete).visibility = View.GONE
//        dialog.setCancelable(true)
        var dialog_tags_id = dialog.findViewById<TagContainerLayout>(R.id.tag_group)
        for (i in selected_tags_list) {
            dialog_tags_id.addTag(i.mname,i.mbol)
        }


        var close = dialog.findViewById<ImageView>(R.id.close_dialog_id)
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

    fun removeTag(position: Int) {
        binding.tagGroup.removeTag(position)
        fav_List.removeAt(position)
        Paper.book().write("fav_tags", fav_List)
        if (fav_List.size.equals(0)) {
            setVisibilities()

        }
    }
    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            FavouriteFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
}