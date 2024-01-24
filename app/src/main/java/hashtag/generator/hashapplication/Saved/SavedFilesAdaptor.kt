package hashtag.generator.hashapplication.Saved

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import co.lujun.androidtagview.TagView
import co.lujun.androidtagview.Tagmodel
import com.project.hashapplication.R
import com.project.hashapplication.databinding.RowForGeneratedTagsBinding
import io.paperdb.Paper
import java.io.File


class SavedFilesAdaptor(
    var context: Context,
    var list_of_file: ArrayList<SaveFragment.FileData>,
    var saveFragment: savedFilesInterface
) :
    RecyclerView.Adapter<SavedFilesAdaptor.viewHolder>() {
    var bounce_animation = AnimationUtils.loadAnimation(context, R.anim.bounce)
//    lateinit var saved:savedFilesInterface

    lateinit var binding: RowForGeneratedTagsBinding
    var total_tags_selected = 0

    lateinit var total_tags_selected_list: ArrayList<Tagmodel>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        binding =
            RowForGeneratedTagsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        total_tags_selected_list = ArrayList<Tagmodel>()

        return viewHolder(binding)
    }

    override fun onBindViewHolder(holder: viewHolder, @SuppressLint("RecyclerView") position: Int) {
        if (list_of_file.size.equals(0)) {
            saveFragment.showNoItem(true)
        }
        addTagsFromFile(position, holder.binding.noOfTagsId)

        holder.binding.cardOptions.visibility = View.VISIBLE
        holder.binding.cardSave.visibility = View.GONE


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

        holder.binding.cardOptions.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val popupMenu = PopupMenu(context, holder.binding.cardOptions)
                popupMenu.menuInflater.inflate(R.menu.menu_more, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                    override fun onMenuItemClick(item: MenuItem?): Boolean {
                        if (item.toString().equals("Rename")) {
                            showRenameDialog(holder.binding.titleTags)

                        } else if (item.toString().equals("Delete")) {
                            deleteFile(position)
                        } else {
                            Toast.makeText(context, "Nothing shown", Toast.LENGTH_SHORT).show()
                        }
                        return true
                    }
                })
                popupMenu.show()
            }

        })


        holder.binding.tagGroup.setOnTagClickListener(object : TagView.OnTagClickListener {
            override fun onTagClick(position: Int, text: String?) {

            }

            override fun onTagLongClick(position: Int, text: String?) {
                holder.binding.checkBox.isChecked = false
                if (holder.binding.tagGroup.toggleSelectTagView(position, false)) {
                    total_tags_selected += 1
                    total_tags_selected_list.add(holder.binding.tagGroup.tags.get(position))
                    saveFragment.show_card_Selected(total_tags_selected_list)
                } else {
                    total_tags_selected --
                    if (total_tags_selected.equals("0")) {
                        saveFragment.showNoItem(true)
                    }
                    total_tags_selected_list.removeAt(position)
                    holder.binding.tagGroup.toggleSelectTagView(position, true)
                    saveFragment.show_card_Selected(total_tags_selected_list)

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
                    addfavData(holder.binding.tagGroup.tags.get(position).mname)
                }

            }

            override fun onTagstarClick(position: Int) {
            }

        })

        holder.binding.checkBox.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                holder.binding.checkBox.startAnimation(bounce_animation)
                var i = 0
                while (i < holder.binding.tagGroup.tags.size) {
                    holder.binding.tagGroup.toggleSelectTagView(i, true)
                    i++
                }
                if (holder.binding.checkBox.isChecked) {
                    total_tags_selected += holder.binding.tagGroup.tags.size
                    for (i in holder.binding.tagGroup.tags.indices) {
                        total_tags_selected_list.add(holder.binding.tagGroup.tags.get(i))
                    }
                    saveFragment.show_card_Selected(total_tags_selected_list)

                } else {
                    for (i in holder.binding.tagGroup.tags.indices) {
                        total_tags_selected_list.remove(holder.binding.tagGroup.tags.get(i))
                    }
                    total_tags_selected_list = ArrayList<Tagmodel>()
                    saveFragment.show_card_Selected(total_tags_selected_list)
                }
            }
        })

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun deleteFile(position: Int) {

        val file = File(list_of_file.get(position).uri)
        file.delete()
        list_of_file.remove(list_of_file.get(position))
        if (list_of_file.size.equals(0)) {
            saveFragment.showNoItem(true)
        }
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, list_of_file.size)
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
                    renameFile(textView.text.toString(), "#" + edited)
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

    private fun renameFile(oldName: String, newName: String) {
        val dir = File(context.getExternalFilesDir("Saved Data"), "")
        if (dir.exists()) {
            val from = File(dir, oldName + ".txt")
            val to = File(dir, newName + ".txt")
            if (from.exists()) {
                from.renameTo(to)
                Toast.makeText(context, "File renamed successfully!!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return list_of_file.size
    }

    class viewHolder(var binding: RowForGeneratedTagsBinding) :
        RecyclerView.ViewHolder(binding.root)

    fun addTagsFromFile(position: Int, textview: TextView) {
        var text = list_of_file.get(position).fileData.split("#")

        var favList = Paper.book().read("fav_tags", ArrayList<String>())
        for (i in text.indices) {
            if (!favList!!.contains("#" + text.get(i).toString())) {
                binding.tagGroup.addTag("#" + text.get(i).toString(), false)
            } else {
                binding.tagGroup.addTag("#" + text.get(i).toString(), true)
            }
            binding.titleTags.text = list_of_file.get(position).filename

        }
        textview.text = text.size.toString()
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

            var intent: Intent = Intent(Intent.ACTION_SEND)
            intent.setType("text/plain")
            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Copied Text");
            intent.putExtra(android.content.Intent.EXTRA_TEXT, joinned_string);
            context.startActivity(Intent.createChooser(intent, "Share"))

        } else {
            Toast.makeText(context, "Can not copy blank text ", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun addfavData(path: String) {
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


}