package com.instaguard.provider.Helpers.CountryCode;

import android.app.Dialog;
import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Window;

import com.instaguard.provider.CustomViews.CustomEditText;
import com.instaguard.provider.CustomViews.CustomTextView;
import com.instaguard.provider.R;

import java.util.List;

/**
 * Created by Uplogic on 27/04/2018.
 */
public class CountryCodeDialog {
    public static void openCountryCodeDialog(CountryCodePicker codePicker) {
        Context context = codePicker.getContext();
        final Dialog dialog = new Dialog(context);
        codePicker.refreshCustomMasterList();
        codePicker.refreshPreferredCountries();
        List<Country> masterCountries = Country.getCustomMasterCountryList(codePicker);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setContentView(R.layout.layout_picker_dialog);

        RecyclerView recyclerView_countryDialog = (RecyclerView) dialog.findViewById(R.id.recycler_countryDialog);

        final CustomTextView textViewTitle = (CustomTextView) dialog.findViewById(R.id.textView_title);
        textViewTitle.setText(codePicker.getDialogTitle());

        final CustomEditText editText_search = (CustomEditText) dialog.findViewById(R.id.editText_search);
        editText_search.setHint(codePicker.getSearchHintText());
        CustomTextView textView_noResult = (CustomTextView) dialog.findViewById(R.id.textView_noresult);
        textView_noResult.setText(codePicker.getNoResultFoundText());

        final CountryCodeAdapter cca = new CountryCodeAdapter(context, masterCountries, codePicker, editText_search, textView_noResult, dialog);
        recyclerView_countryDialog.setLayoutManager(new LinearLayoutManager(context));
        recyclerView_countryDialog.setAdapter(cca);
        dialog.show();
    }
}
