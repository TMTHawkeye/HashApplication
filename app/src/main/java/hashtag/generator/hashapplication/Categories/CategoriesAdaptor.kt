package hashtag.generator.hashapplication.Categories//package com.project.hashapplication.Categories

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Filter
import android.widget.Filterable
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import hashtag.generator.hashapplication.Home.GenerateTagsActivity
import hashtag.generator.hashapplication.ModelClasses.categoriesData
import com.project.hashapplication.R
import com.project.hashapplication.databinding.RowForCategoriesFragmentBinding
import java.util.*


class CategoriesAdaptor(var context: Context, var categories_list: ArrayList<categoriesData>) :
    RecyclerView.Adapter<CategoriesAdaptor.viewHolder>(), Filterable {

    var bounce_animation = AnimationUtils.loadAnimation(context, R.anim.bounce)

    var filtered_categories: ArrayList<categoriesData> = ArrayList(categories_list)
    lateinit var binding: RowForCategoriesFragmentBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        binding = RowForCategoriesFragmentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return viewHolder(binding)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: viewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.binding.titleOfCategory.text = categories_list.get(position).title_category
        Glide.with(context).load(categories_list.get(position).image_category)
            .into(holder.binding.imageOfCategory)

        var filter=categories_list.get(position).colors_list
        val newColor = ColorUtils.blendARGB(Color.parseColor(filter),R.color.black, 0.22f)

        holder.binding.cardViewOfCategory.setCardBackgroundColor(Color.parseColor(filter))
        holder.binding.imageOfCategory.setColorFilter(newColor)
        holder.binding.imagedot.setColorFilter(newColor)

        holder.binding.cardViewOfCategory.setCardBackgroundColor(
            Color.parseColor(
                categories_list.get(
                    position
                ).colors_list
            )
        )

        holder.binding.cardViewOfCategory.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                holder.binding.cardViewOfCategory.startAnimation(bounce_animation)
                context.startActivity(
                    Intent(
                        context,
                        GenerateTagsActivity::class.java
                    ).putExtra("tagNameList", categories_list.get(position).title_category)
                        .putExtra("joinned_tag", "from_categories")
                )
            }

        })


    }

    override fun getItemCount(): Int {
        return categories_list.size
    }

    override fun getFilter(): Filter? {
        return searchFilter()
    }


    fun searchFilter(): Filter? {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val FilteredArrList: ArrayList<categoriesData> = ArrayList()
                if (constraint == "" || constraint.toString().isEmpty()) {
                    FilteredArrList.addAll(filtered_categories)
                } else {
                    val filterPattern =
                        constraint.toString().lowercase(Locale.getDefault()).trim { it <= ' ' }
                            .replace(" ", "")
                    for (i in filtered_categories) {
                        if (i.title_category.lowercase().contains(filterPattern)) {
                            FilteredArrList.add(i)
                        }
                    }
                }
                val results = FilterResults()

                results.values = FilteredArrList
                return results
            }

            @SuppressWarnings("unchecked")
            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                categories_list.clear()
                categories_list.addAll(results.values as ArrayList<categoriesData>)
                notifyDataSetChanged() // notifies the data with new filtered values
            }
        }
    }

    class viewHolder(val binding: RowForCategoriesFragmentBinding) :
        RecyclerView.ViewHolder(binding.root)
}