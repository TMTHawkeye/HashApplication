package hashtag.generator.hashapplication.Home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.hashapplication.databinding.RowForRecentHomeBinding


class RecentTagsAdaptor(
    val context: Context,
    val tagsList: ArrayList<PredictedTagsAdaptor.RecentData>
) :
    RecyclerView.Adapter<RecentTagsAdaptor.viewHolder>() {

    private val limit = 20

    lateinit var binding: RowForRecentHomeBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        binding =
            RowForRecentHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return viewHolder(binding)
    }

    override fun onBindViewHolder(holder: viewHolder, @SuppressLint("RecyclerView") position: Int) {
        if (tagsList != null) {
            val tagName: String = tagsList[position].name
            val tagCount: String = tagsList[position].n_o_tags

            holder.binding.tagNameInRow.text = setColorforTitle(tagName, 0, 1)
            holder.binding.noOfTagsId.text = tagCount

            holder.binding.cardRecentTag.setOnClickListener(object : OnClickListener {
                override fun onClick(p0: View?) {
                    context.startActivity(
                        Intent(
                            context,
                            GenerateTagsActivity::class.java
                        ).putExtra("tagNameList", holder.binding.tagNameInRow.text.toString())
                            .putExtra("joinned_tag", "from_recent")
                    )
                }
            })
        }
    }

    fun setColorforTitle(text: String, start: Int, end: Int): SpannableString {
        val spannableString = SpannableString(text)
        var hashcolor = Color.parseColor("#FFA5A5")
        var txtcolor = Color.parseColor("#FFFFFFFF")
        val pink = ForegroundColorSpan(hashcolor)
        val white = ForegroundColorSpan(txtcolor)
        spannableString.setSpan(
            pink,
            start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannableString
    }


    override fun getItemCount(): Int {
        if (tagsList.size > limit) {
            return limit;
        } else {
            return tagsList.size
        }
    }

    class viewHolder(val binding: RowForRecentHomeBinding) : RecyclerView.ViewHolder(binding.root)
}