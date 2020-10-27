package com.example.sherazali.idpos_app.Adapters

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sherazali.idpos_app.R
import com.example.sherazali.idpos_app.Retailer.StoresSuggestions
import kotlinx.android.synthetic.main.suggestions_adapter.view.*
import java.lang.Exception

class SuggestionsAdapter(var storesSuggestions: StoresSuggestions) : RecyclerView.Adapter<SuggestionCustomerHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, pos: Int): SuggestionCustomerHolder {


        var layoutInflater = LayoutInflater.from(parent.context)

        var suggestionslayout = layoutInflater.inflate(R.layout.suggestions_adapter, parent, false)

        return SuggestionCustomerHolder(suggestionslayout)
    }

    override fun getItemCount(): Int {

        return storesSuggestions.suggestions.count()
    }

    override fun onBindViewHolder(holder: SuggestionCustomerHolder, pos: Int) {

        try {
            var storeSuggestion = storesSuggestions.suggestions.get(pos)

            holder.view.suggestedProductName_txt.text = storeSuggestion.ProductName
            holder.view.suggestedProductCategory_txt.text = storeSuggestion.CategoryName
            holder.view.suggestedProductCount_txt.text = storeSuggestion.ProductCount.toString()

        }catch (e:Exception){
            Log.e("Suggestion Adapte Err", "$e")
        }
    }

}

class SuggestionCustomerHolder(var view:View) : RecyclerView.ViewHolder(view)