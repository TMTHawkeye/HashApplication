package hashtag.generator.hashapplication.Home

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import co.lujun.androidtagview.TagContainerLayout
import co.lujun.androidtagview.TagView
import co.lujun.androidtagview.Tagmodel
import com.cunoraz.tagview.Tag
import hashtag.generator.hashapplication.ModelClasses.TagClass
import com.project.hashapplication.R
import com.project.hashapplication.databinding.RowForGeneratedTagsBinding
import io.paperdb.Paper
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList


class PredictedTagsAdaptor(
    var context: Context,
    var tagArrayList: ArrayList<String>,
    var intent_from: String
) :
    RecyclerView.Adapter<PredictedTagsAdaptor.viewHolder>() {
    var all_tags_total_selected: Int = 0
    lateinit var selected_tags_list: java.util.ArrayList<Tagmodel>

    var mExecutorService = Executors.newSingleThreadExecutor()
    val handler = Handler(Looper.getMainLooper())

    var bounce_animation = AnimationUtils.loadAnimation(context, R.anim.bounce)

    lateinit var tagList: ArrayList<TagClass?>
    lateinit var list_to_dlt: ArrayList<Int>

    lateinit var binding: RowForGeneratedTagsBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        binding =
            RowForGeneratedTagsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        tagList = ArrayList<TagClass?>()
        //for search tags case
        if (!intent_from.equals("from_categories") && !intent_from.equals("from_recent")) {
            prepareTags()
        }
        list_to_dlt = ArrayList()
        selected_tags_list = ArrayList<Tagmodel>()
        return viewHolder(binding)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        if (intent_from.equals("from_categories") || intent_from.equals("from_recent")) {
            if (intent_from.equals("from_categories")) {
                holder.binding.titleTags.visibility = View.GONE
            }
            preparedTags(position)
        }
        setIndividualTags(position, holder.binding.tagGroup)
        if (!intent_from.equals("from_categories")) {
            holder.binding.titleTags.text = "#" + tagArrayList.get(position)
            holder.binding.tagGroup.addTag("#" + tagArrayList.get(position), false)
        }
        var no_tags_text = holder.binding.tagGroup.tags.size
        holder.binding.noOfTagsId.text = no_tags_text.toString() + " Tags"
        if (!intent_from.equals("from_categories")) {
            var tagsList: ArrayList<RecentData>? = Paper.book().read("no_of_tags")
            if (tagsList != null) {
                var recentData =
                    RecentData(
                        binding.titleTags.text.toString(),
                        binding.noOfTagsId.text.toString()
                    )
                tagsList.add(0, recentData)
                Paper.book().write("no_of_tags", tagsList);
            } else {
                tagsList = ArrayList()
                var recentData =
                    RecentData(
                        binding.titleTags.text.toString(),
                        binding.noOfTagsId.text.toString()
                    )
                tagsList.add(0, recentData)
                Paper.book().write("no_of_tags", tagsList);
            }
        }
        holder.binding.tagGroup.setOnTagClickListener(object : TagView.OnTagClickListener {
            override fun onTagClick(position: Int, text: String?) {
            }

            override fun onTagLongClick(position: Int, text: String?) {
                var activity = (context as GenerateTagsActivity)
                holder.binding.checkBox.isChecked = false
                if (holder.binding.tagGroup.toggleSelectTagView(position, false)) {
                    all_tags_total_selected += 1
                    activity.binding.cardSelectedTags1.visibility = View.VISIBLE
                    activity.binding.textSelectedTags.text = all_tags_total_selected.toString()
                    selected_tags_list += holder.binding.tagGroup.tags.get(position)
                } else {
                    all_tags_total_selected -= 1
                    activity.binding.textSelectedTags.text = all_tags_total_selected.toString()
                    if (activity.binding.textSelectedTags.text.equals("0")) {
                        activity.binding.textSelectedTags.text = "1"
                        activity.binding.cardSelectedTags1.visibility = View.GONE
                    }
                    selected_tags_list.remove(holder.binding.tagGroup.tags.get(position))
                    holder.binding.tagGroup.toggleSelectTagView(position, true)
                }
            }

            override fun onSelectedTagDrag(position: Int, text: String?) {
            }

            override fun onTagCrossClick(position: Int) {
                var favList = Paper.book().read("fav_tags", ArrayList<String>())
                if (favList!!.contains(holder.binding.tagGroup.tags.get(position).mname)) {
                    holder.binding.tagGroup.chdrawable(
                        position,
                        holder.binding.tagGroup.tags.get(position).mname,
                        false
                    )
                    favList.remove(holder.binding.tagGroup.tags.get(position).mname)
                    Paper.book().write("fav_tags", favList)
                } else {
                    holder.binding.tagGroup.chdrawable(
                        position,
                        holder.binding.tagGroup.tags.get(position).mname,
                        true
                    )
                    addToDB(holder.binding.tagGroup.tags.get(position).mname)

                }
            }

            override fun onTagstarClick(position: Int) {
            }
        })


        holder.binding.cardCopy.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                holder.binding.cardCopy.startAnimation(bounce_animation)

                var joinned_string = ""
                Log.d("TAG arr", "onClick: " + binding.tagGroup.tags.size)
                for (i in 0 until holder.binding.tagGroup.tags.size) {
                    joinned_string += holder.binding.tagGroup.tags.get(i).mname.toString() + " "
                }
                copyTags(joinned_string)
            }
        })

        holder.binding.checkBox.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                var activity = (context) as GenerateTagsActivity

                if (holder.binding.checkBox.isChecked) {
                    var i = 0
                    while (i < holder.binding.tagGroup.tags.size) {
                        holder.binding.tagGroup.toggleSelectTagView(i, true)
                        i++
                    }
                    all_tags_total_selected = 0
                    all_tags_total_selected += holder.binding.tagGroup.tags.size
                    activity.binding.cardSelectedTags1.visibility = View.VISIBLE
                    activity.binding.textSelectedTags.text =
                        all_tags_total_selected.toString()
                    selected_tags_list += holder.binding.tagGroup.tags
                } else {
                    var i = 0
                    while (i < holder.binding.tagGroup.tags.size) {
                        holder.binding.tagGroup.toggleSelectTagView(i, false)
                        i++
                    }
                    all_tags_total_selected -= holder.binding.tagGroup.tags.size
                    activity.binding.textSelectedTags.text = all_tags_total_selected.toString()
                    if (activity.binding.textSelectedTags.text.equals("1") || activity.binding.textSelectedTags.text.equals(
                            "0"
                        )
                    ) {
                        activity.binding.textSelectedTags.text = "1"
                        activity.binding.cardSelectedTags1.visibility = View.GONE
                        selected_tags_list = ArrayList<Tagmodel>()

                        var i = 0
                        while (i < holder.binding.tagGroup.tags.size) {
                            holder.binding.tagGroup.toggleSelectTagView(i, true)
                            i++
                        }
                    }
                }
            }
        })

        holder.binding.titleTags.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                holder.binding.titleTags.startAnimation(bounce_animation)
                showRenameDialog(holder.binding.titleTags)
            }
        })

        holder.binding.cardSave.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                holder.binding.cardSave.startAnimation(bounce_animation)
                var tags_in_group = ""
                var i = 0
                while (i < holder.binding.tagGroup.tags.size) {
                    tags_in_group += holder.binding.tagGroup.tags.get(i).mname.toString()
                    i++
                }
                saveTags(tags_in_group, holder.binding.titleTags.text.toString())
            }
        })
        var activity = context as GenerateTagsActivity
        activity.binding.cardSelectedTags1.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                activity.binding.cardSelectedTags1.startAnimation(bounce_animation)
                showSelectedTagsDialog(activity)
            }
        })
    }

    private fun showRenameDialog(textView: TextView) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_rename_card)
        dialog.window!!.attributes.gravity = Gravity.CENTER;
        dialog.setCancelable(true)

        dialog.window!!.setBackgroundDrawableResource(com.google.android.material.R.color.mtrl_btn_transparent_bg_color)

        var editedText = dialog.findViewById<EditText>(R.id.edit_text_dialog)
        var save_btn = dialog.findViewById<CardView>(R.id.save)
        var cancel_btn = dialog.findViewById<CardView>(R.id.cancel)
        var cross_btn = dialog.findViewById<ImageView>(R.id.close_dialog_id)
        save_btn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                Log.d("TAG saved tag", "onClick: " + editedText.text.toString())
                save_btn.startAnimation(bounce_animation)
                if (!editedText.text.isBlank()) {
                    var edited = editedText.text.toString().trim()
                    textView.text = "#" + edited
                    dialog.dismiss()
                } else {
                    Toast.makeText(dialog.context, "Name can not be empty", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        })
        cancel_btn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                cancel_btn.startAnimation(bounce_animation)
                dialog.dismiss()
            }
        })
        cross_btn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                cross_btn.startAnimation(bounce_animation)
                dialog.dismiss()
            }
        })
        dialog.show()

    }

    override fun getItemCount(): Int {
        return tagArrayList.size
    }

    private fun preparedTags(position: Int) {
        if (!intent_from.equals("from_recent")) {
            val obj_categories_name = JSONObject(loadJSONFromAsset())
            var categories_name_list = obj_categories_name.getJSONArray(tagArrayList.get(position))
            tagList = java.util.ArrayList()
            try {
                for (i in 0 until categories_name_list.length()) {
                    var temp = categories_name_list.get(i)
                    tagList.add(
                        TagClass(
                            temp.toString(), false
                        )
                    )
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            prepareTags()
        }
    }

    fun loadJSONFromAsset(): String? {
        var json: String? = null
        try {
            val `is`: InputStream = context.assets.open("hashtags.json")
            val size: Int = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            val charset: Charset = Charsets.UTF_8
            json = String(buffer, charset)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    fun loadCategoriesJSONFromAsset(): String? {
        var json: String? = null
        try {
            val `is`: InputStream = context.assets.open("categories_names.json")
            val size: Int = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            val charset: Charset = Charsets.UTF_8
            json = String(buffer, charset)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    private fun prepareTags() {

        //load categories names from json
        val obj_categories_name = JSONObject(loadCategoriesJSONFromAsset())

        //load tags from json
        val obj_categories = JSONObject(loadJSONFromAsset())

        //get json object from categories
        var categories_name_list = obj_categories_name.getJSONArray("Categories")

        //arraylist of names of categories , images excluded
        var final_categories_names = ArrayList<String>()
        for (i in 0 until categories_name_list.length()) {
            var each_object_of_list = categories_name_list.getJSONObject(i)
            var name = each_object_of_list.getString("name")
            final_categories_names.add(name)
        }

        // array list of all tags of all categories
        var final_all_tags_list = ArrayList<String>()
        for (i in 0 until final_categories_names.size) {
            var categories_tags_list = obj_categories.getJSONArray(final_categories_names.get(i))
            for (i in 0 until categories_tags_list.length()) {
                final_all_tags_list.add(categories_tags_list.get(i).toString())
                tagList.add(TagClass(final_all_tags_list.get(i), false))
            }
        }
    }

    private fun setIndividualTags(position: Int, tagGroup: TagContainerLayout) {
        var favList = Paper.book().read("fav_tags", ArrayList<String>())
        var j = 0
        val text = tagArrayList.get(position)
        var tag: Tag
        for (i in tagList) {
            if (!intent_from.equals("from_categories")) {
                val filterPattern =
                    text.lowercase(Locale.getDefault()).trim { it <= ' ' }
                        .replace(" ", "")
                if (i!!.name.lowercase()
                        .contains(filterPattern)
                ) {
                    Log.d("TAG list", "setIndividualTags: " + i!!.name.toString())
                    tag = Tag("#" + i!!.name)
                    tag.radius = 10f
                    tag.isDeletable = true
                    var list= tagGroup.tags2
                    if (!list.contains(tag.text.toString())) {
                        if (favList!!.contains(tag.text.toString())) {
                            tagGroup.addTag(tag.text.toString(), true)
                        } else {
                            tagGroup.addTag(tag.text.toString(), false)
                        }
                    }else{
                        Log.d("TAG", "setIndividualTags:")
                    }
                }
            } else {
                tag = Tag("#" + i!!.name)
                tag.radius = 10f
                tag.isDeletable = true
//                array_of_tags.add(tag)
                if (favList!!.contains(tag.text.toString())) {
                    tagGroup.addTag(tag.text.toString(), true)
                } else {
                    tagGroup.addTag(tag.text.toString(), false)
                }
            }
            j++

        }
    }

    fun copyTags(joinned_string: String) {
        if (!joinned_string.isBlank()) {
            val clipboard =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip =
                ClipData.newPlainText("label_quote", joinned_string.toString())
            Toast.makeText(context, "Copied to clipboard ", Toast.LENGTH_SHORT)
                .show()
            clipboard.setPrimaryClip(clip)
        } else {
            Toast.makeText(context, "Can not copy blank text ", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun saveTags(sFileName: String, title: String) {
        val directory = File(context.getExternalFilesDir("Saved Data"), "")
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

    @SuppressLint("SuspiciousIndentation")
    fun showSelectedTagsDialog(activity: Context) {
        val dialog = Dialog(activity)
        dialog.setContentView(R.layout.dialogue_selected_tags)
        dialog.window!!.attributes.gravity = Gravity.CENTER;
        dialog.window!!.setBackgroundDrawableResource(com.google.android.material.R.color.mtrl_btn_transparent_bg_color)

//        dialog.setCancelable(true)
        var dialog_tags_id = dialog.findViewById<TagContainerLayout>(R.id.tag_group)
        for (i in selected_tags_list) {
            dialog_tags_id.isEnableCross = false
            dialog_tags_id.isLongClickable = true
            dialog_tags_id.addTag(i.mname, i.mbol)
        }
        dialog_tags_id.setOnTagClickListener(object :
            co.lujun.androidtagview.TagView.OnTagClickListener {
            override fun onTagClick(position: Int, text: String?) {
                if (dialog_tags_id.toggleSelectTagView(position, true)) {
                    list_to_dlt.add(position)
                    dialog_tags_id.toggleSelectTagView(position, false)

                } else if (dialog_tags_id.toggleSelectTagView(position, false)) {
                    list_to_dlt.remove(position)
                    dialog_tags_id.toggleSelectTagView(position, true)
                }
            }

            override fun onTagLongClick(position: Int, text: String?) {

            }

            override fun onSelectedTagDrag(position: Int, text: String?) {
            }

            override fun onTagCrossClick(position: Int) {
            }

            override fun onTagstarClick(position: Int) {
                dialog_tags_id.tags.removeAt(position)

            }

        })
        var close = dialog.findViewById<ImageView>(R.id.close_dialog_id)
        close.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                close.startAnimation(bounce_animation)
                dialog.dismiss()
            }
        })
        val dialog_card_copy = dialog.findViewById<CardView>(R.id.card_copy)
        dialog_card_copy.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                dialog_card_copy.startAnimation(bounce_animation)

                var joinned_string = ""
                for (i in 0 until dialog_tags_id.tags.size) {
                    joinned_string += dialog_tags_id.tags.get(i).mname.toString() + " "
                }
                copyTags(joinned_string)
            }

        })
        val dialog_card_delete = dialog.findViewById<CardView>(R.id.card_delete)
        dialog_card_delete.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
//                Toast.makeText(context, list_to_dlt.size.toString(), Toast.LENGTH_SHORT).show()
                var i = 0
                while (i < list_to_dlt.size) {
                    var index = list_to_dlt.get(i)
//                    Toast.makeText(context, index.toString(), Toast.LENGTH_SHORT).show()
                    dialog_tags_id.removeTag(index)
                    i++
                }
            }

        })
        val dialog_card_save = dialog.findViewById<CardView>(R.id.card_save)
        dialog_card_save.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                dialog_card_save.startAnimation(bounce_animation)

                var joinned_string = ""
                for (i in 0 until dialog_tags_id.tags.size) {
                    joinned_string += dialog_tags_id.tags.get(i).mname.toString() + " "
                }
                saveTags(joinned_string, "")
            }

        })
        dialog.show()
    }

    fun addToDB(path: String) {
        val pathDB = Paper.book().read("fav_tags", java.util.ArrayList<String>())
        val duplicateDB = java.util.ArrayList<String>()
        if (pathDB == null) {
            duplicateDB.add(path)
            Paper.book().write("fav_tags", duplicateDB)
        } else {
            if (pathDB.contains(path)) {
                pathDB.remove(path)
            }
            pathDB.add(path)
            Paper.book().write("fav_tags", pathDB)
        }
    }

    class RecentData(val name: String, val n_o_tags: String)

    class viewHolder(val binding: RowForGeneratedTagsBinding) :
        RecyclerView.ViewHolder(binding.root)
}

