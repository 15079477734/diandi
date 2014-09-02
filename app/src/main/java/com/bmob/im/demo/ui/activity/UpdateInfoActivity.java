package com.bmob.im.demo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.bmob.im.demo.R;
import com.bmob.im.demo.config.Constant;
import com.bmob.im.demo.view.HeaderLayout.onRightImageButtonClickListener;

public class UpdateInfoActivity extends ActivityBase {

    EditText mEditText;
    TextView mTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findView();
        initView();
    }

    private void findView() {
        setContentView(R.layout.activity_set_updateinfo);
        mEditText = (EditText) findViewById(R.id.activity_updateinfo_edittext);
        mTextView = (TextView) findViewById(R.id.activity_updateinfo_text);
    }

    private void initView() {
        Intent intent = getIntent();
        String actionbarName = intent.getStringExtra(Constant.UPDATE_ACTIONBAR_NAME);
        mTextView.setText(intent.getStringExtra(Constant.UPDATE_TEXT));
        mEditText.setHint(intent.getStringExtra(Constant.UPDATE_EDIT_HINT));
        initTopBarForBoth(actionbarName, R.drawable.base_action_bar_true_bg_selector,
                new onRightImageButtonClickListener() {
                    @Override
                    public void onClick() {
                        String backStr = mEditText.getText().toString();
                        if (backStr.equals("")) {
                            ShowToast("不能为空");
                            return;
                        }
                        Intent intentBack = new Intent();
                        intentBack.putExtra(Constant.UPDATE_BACK_CONTENT, backStr);
                        setResult(RESULT_OK, intentBack);
                        finish();
                    }
                }
        );
    }
}
