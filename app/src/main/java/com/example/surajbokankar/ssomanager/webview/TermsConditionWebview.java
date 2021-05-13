package com.example.surajbokankar.ssomanager.webview;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.example.surajbokankar.ssomanager.R;

/**
 * Created by suraj.bokankar on 24/05/18.
 */

public class TermsConditionWebview extends android.app.DialogFragment {



    WebView webView=null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.layout_consent_web_view,container,false);
        init(view);
        return view;
    }

    private void init(View view) {
       webView=view.findViewById(R.id.web_view);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        intiViews();
    }

    private void intiViews() {
      if(getArguments()!=null){
          String url=getArguments().getString("Consent");
          webView.loadUrl(url);
      }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.homeStyle);
    }
}
