package cn.ieclipse.af.musdk.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.baidu.autoupdatesdk.AppUpdateInfo;
import com.baidu.autoupdatesdk.AppUpdateInfoForInstall;
import com.baidu.autoupdatesdk.BDAutoUpdateSDK;
import com.baidu.autoupdatesdk.CPCheckUpdateCallback;
import com.baidu.autoupdatesdk.CPUpdateDownloadCallback;
import com.baidu.autoupdatesdk.UICheckUpdateCallback;
import com.qihoo.appstore.common.updatesdk.lib.UpdateHelper;

public class MainActivity extends Activity implements View.OnClickListener {

    private TextView txtLog;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.bd_btn_ui).setOnClickListener(this);
        findViewById(R.id.bd_btn_silence).setOnClickListener(this);
        findViewById(R.id.bd_btn_no_ui).setOnClickListener(this);

        findViewById(R.id.qh_btn_ui).setOnClickListener(this);
        findViewById(R.id.qh_btn_silence).setOnClickListener(this);
        findViewById(R.id.qh_btn_no_ui).setOnClickListener(this);

        txtLog = (TextView) findViewById(R.id.txt_log);
        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bd_btn_ui:
                txtLog.setText("");
                dialog.show();
                BDAutoUpdateSDK.cpUpdateCheck(this, new BDCPCheckUpdateCallback(), true);
                break;
            case R.id.bd_btn_silence:
                txtLog.setText("");
                BDAutoUpdateSDK.silenceUpdateAction(this, true);
                break;
            case R.id.bd_btn_no_ui:
                txtLog.setText("");
                dialog.show();
                BDAutoUpdateSDK.uiUpdateAction(this, new BDUICheckUpdateCallback(), true);
                break;
            case R.id.qh_btn_ui:
                txtLog.setText("");
                qh360(0);
                break;
            case R.id.qh_btn_silence:
                txtLog.setText("");
                qh360(1);
                break;
            case R.id.qh_btn_no_ui:
                txtLog.setText("");
                qh360(2);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        dialog.dismiss();
        super.onDestroy();
    }

    private void showToast(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg).create().show();
    }

    private class BDUICheckUpdateCallback implements UICheckUpdateCallback {

        @Override
        public void onNoUpdateFound() {
            dialog.dismiss();
            txtLog.setText("未发现新版本，您在百度市场上的应用已是最新");
        }

        @Override
        public void onCheckComplete() {
            dialog.dismiss();
        }
    }

    private class BDCPCheckUpdateCallback implements CPCheckUpdateCallback {

        @Override
        public void onCheckUpdateCallback(final AppUpdateInfo info, AppUpdateInfoForInstall infoForInstall) {
            if (infoForInstall != null && !TextUtils.isEmpty(infoForInstall.getInstallPath())) {
                txtLog.setText(txtLog.getText() + "\n install info: " + infoForInstall.getAppSName() + ", \nverion="
                    + infoForInstall.getAppVersionName() + ", \nchange log=" + infoForInstall.getAppChangeLog());
                txtLog.setText(
                    txtLog.getText() + "\n we can install the apk file in: " + infoForInstall.getInstallPath());
                BDAutoUpdateSDK.cpUpdateInstall(getApplicationContext(), infoForInstall.getInstallPath());
            }
            else if (info != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                long size = info.getAppPathSize() > 0 ? info.getAppPathSize() : info.getAppSize();
                builder.setTitle(info.getAppVersionCode() + ", " + byteToMb(size)).setMessage(
                    Html.fromHtml(info.getAppChangeLog())).setNeutralButton("普通升级", null).setPositiveButton("智能升级",
                    null).setCancelable(info.getForceUpdate() != 1).setOnKeyListener(
                    new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                return true;
                            }
                            return false;
                        }
                    });
                if (info.getForceUpdate() != 1) {
                    builder.setNegativeButton("暂不升级", null);
                }
                AlertDialog dialog = builder.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        BDAutoUpdateSDK.cpUpdateDownloadByAs(MainActivity.this);
                    }
                });
                dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BDAutoUpdateSDK.cpUpdateDownload(MainActivity.this, info, new UpdateDownloadCallback());
                    }
                });
            }
            else {
                txtLog.setText(txtLog.getText() + "\n no update.");
            }

            dialog.dismiss();
        }

        private String byteToMb(long fileSize) {
            float size = ((float) fileSize) / (1024f * 1024f);
            return String.format("%.2fMB", size);
        }
    }

    private class UpdateDownloadCallback implements CPUpdateDownloadCallback {

        @Override
        public void onDownloadComplete(String apkPath) {
            txtLog.setText(txtLog.getText() + "\n onDownloadComplete: " + apkPath);
            BDAutoUpdateSDK.cpUpdateInstall(getApplicationContext(), apkPath);
        }

        @Override
        public void onStart() {
            txtLog.setText(txtLog.getText() + "\n Download onStart");
        }

        @Override
        public void onPercent(int percent, long rcvLen, long fileSize) {
            txtLog.setText(txtLog.getText() + "\n Download onPercent: " + percent + "%");
        }

        @Override
        public void onFail(Throwable error, String content) {
            txtLog.setText(txtLog.getText() + "\n Download onFail: " + content);
        }

        @Override
        public void onStop() {
            txtLog.setText(txtLog.getText() + "\n Download onStop");
        }
    }

    // 360
    private void qh360(int type) {
        Context context = this;
        UpdateHelper.getInstance().init(context, context.getResources().getColor(R.color.colorPrimary));
        UpdateHelper.getInstance().setDebugMode(true);
        if (type == 0) {
            txtLog.setText("此功能未添加，若想添加，请将../doc/libarmeabi.so复制到您的app jniLib（libs/armeabi）目录下");
        }
        else if (type == 1) {
            long intervalMillis = 10 * 1000L;           //第一次调用startUpdateSilent出现弹窗后，如果10秒内进行第二次调用不会查询更新
            UpdateHelper.getInstance().autoUpdate(context.getPackageName(), false, intervalMillis);
        }
        else {
            UpdateHelper.getInstance().manualUpdate(context.getPackageName());
        }
    }
}
