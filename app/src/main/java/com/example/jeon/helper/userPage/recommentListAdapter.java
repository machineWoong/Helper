package com.example.jeon.helper.userPage;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.jeon.helper.R;
import com.example.jeon.helper.ip;
import com.example.jeon.helper.selectMenu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by JEON on 2018-05-24.
 */

public class recommentListAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<recomment_Data> recoData = new ArrayList<>();
    Context context;
    String loginUserId;
    String targetId;

    String data;

    com.example.jeon.helper.ip ip = new ip();
    String ipad = ip.getIp();

    public recommentListAdapter(ArrayList<recomment_Data> recoData, Context context, String loginUserId,String targetId) {
        this.recoData = recoData;
        this.context = context;
        this.loginUserId = loginUserId;
        this.targetId = targetId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.userpage_re_recomment_item, parent, false);
        return new RowCell(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        // 프로필 이미지 뿌려주기
        if (recoData.get(position).recomentProfile.equals("이미지 없음")  ) {

        } else {
            if (recoData.get(position).recomentProfile.contains("http://k.kakaocdn.net")) {
                Glide.with(context).load(recoData.get(position).recomentProfile).into(((RowCell) holder).re_comment_user_profile);
            } else {
                Glide.with(context).load(ipad + "/" + recoData.get(position).recomentProfile).into(((RowCell) holder).re_comment_user_profile);
            }
        }



        // 닉네임 날짜 내용 댓글 갯수
        ((RowCell) holder).re_comment_user_nickName.setText(recoData.get(position).recomentNick);
        ((RowCell) holder).re_comment_date.setText(recoData.get(position).recomentDate);
        ((RowCell) holder).re_comment_content.setText(recoData.get(position).recomentContent);


        // 접속자에 따른 버튼 숨기기 등.
        if( !loginUserId.equals(recoData.get(position).recomentId)){
            // 다른인간
            ((RowCell) holder).re_comment_edit.setVisibility(View.VISIBLE);
            ((RowCell) holder).re_comment_edit.setVisibility(View.GONE);

            ((RowCell) holder).re_comment_delete.setVisibility(View.VISIBLE);
            ((RowCell) holder).re_comment_delete.setVisibility(View.GONE);


        } else if ( loginUserId.equals(recoData.get(position).recomentId)) { // 같은인간
            ((RowCell) holder).re_comment_edit.setVisibility(View.VISIBLE);
            ((RowCell) holder).re_comment_delete.setVisibility(View.VISIBLE);
        }


        // 대댓글 수정
        edit_re_recomment(position,holder);

        // 삭제 버튼
        delet_re_recomment(position,holder);

        ((RowCell)holder).re_comment_user_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoSelect = new Intent(context,selectMenu.class);
                gotoSelect.putExtra("loginUserId",loginUserId);
                gotoSelect.putExtra("targetId",recoData.get(position).recomentId);
                ((showMoreRiple)context).startActivity(gotoSelect);
            }
        });


    }

    @Override
    public int getItemCount() {
        return recoData.size();
    }


    private static class RowCell extends RecyclerView.ViewHolder {

        public ImageView re_comment_user_profile;
        public TextView re_comment_user_nickName, re_comment_date, re_comment_edit, re_comment_delete, re_comment_content;
        public LinearLayout re_re_comment_layout;

        public EditText re_comment_editText;
        public TextView edit_re_commentEditBtn, edit_re_commentEdit_cancel;


        public RowCell(View view) {
            super(view);
            re_comment_user_profile = (ImageView) view.findViewById(R.id.re_comment_user_profile);
            re_comment_user_nickName = (TextView) view.findViewById(R.id.re_comment_user_nickName);
            re_comment_date = (TextView) view.findViewById(R.id.re_comment_date);

            re_comment_edit = (TextView) view.findViewById(R.id.re_comment_edit);
            re_comment_delete = (TextView) view.findViewById(R.id.re_comment_delete);
            re_comment_content = (TextView) view.findViewById(R.id.re_comment_content);

            re_re_comment_layout = (LinearLayout)view.findViewById(R.id.re_re_comment_layout);
            edit_re_commentEditBtn = (TextView)view.findViewById(R.id.edit_re_commentEditBtn);
            edit_re_commentEdit_cancel = (TextView)view.findViewById(R.id.edit_re_commentEdit_cancel);
            re_comment_editText = (EditText)view.findViewById(R.id.re_comment_editText);
        }
    }

    // 수정하기 버튼
    public void edit_re_recomment(final int position, final RecyclerView.ViewHolder holder){

        // 수정버튼 클릭
        ((RowCell) holder).re_comment_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 레이아웃 보여주기
                ((RowCell) holder).re_re_comment_layout.setVisibility(View.VISIBLE);

                // edittext에 뿌려주기
                ((RowCell) holder).re_comment_editText.setText(recoData.get(position).recomentContent);
            }
        });


        // 변경 버튼
        ((RowCell) holder).edit_re_commentEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 내용가지고 오기
                data = ((RowCell) holder).re_comment_editText.getText().toString();

                if ( data == null || data.equals("") || TextUtils.isEmpty(data)){
                    Toast.makeText(context, "수정할 내용을 입력해주세요", Toast.LENGTH_SHORT).show();
                }else{

                    // 서버에 전송
                    make_Re_recomment(data,2,recoData.get(position).recomentNo);

                    // 내용 변경
                    recoData.get(position).recomentContent = data;
                    ((RowCell) holder).re_comment_editText.setText(recoData.get(position).recomentContent);


                    // 레이아웃 숨기기
                    ((RowCell) holder).re_re_comment_layout.setVisibility(View.VISIBLE);
                    ((RowCell) holder).re_re_comment_layout.setVisibility(View.GONE);

                    ((RowCell) holder).re_comment_editText.setText(null);

                    notifyItemChanged(position);
                }

            }
        });

        // 취소하기 버튼
        ((RowCell) holder).edit_re_commentEdit_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 레이아웃 숨기기
                ((RowCell) holder).re_re_comment_layout.setVisibility(View.VISIBLE);
                ((RowCell) holder).re_re_comment_layout.setVisibility(View.GONE);
            }
        });




    }

    // 삭제하기 버튼
    public void delet_re_recomment(final int position, final RecyclerView.ViewHolder holder){

        ((RowCell)holder).re_comment_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder ab = new AlertDialog.Builder(context);
                ab.setTitle("댓글 삭제");
                ab.setMessage("등록하신 댓글을 삭제하시겠습니까?");

                ab.setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                ab.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        make_Re_recomment("삭제된 글입니다",3,recoData.get(position).recomentNo);

                        recoData.remove(position);
                        notifyDataSetChanged();

                        ((showMoreRiple)context).minCount();

                    }
                });

                ab.show();
            }
        });

    }



    // (서버 전송 ) 대댓글 편집  ( 1답글추가 2대댓글 수정 3대댓글 삭제 )
    public void make_Re_recomment(String content,int mode,int commentNo){
        class getRepleDataHttp extends AsyncTask<Void,Void,String> {

            String result2;
            ProgressDialog dialog = new ProgressDialog(context);

            String loginUserId;
            String targetId;
            String content;
            int commentNo;
            int mode;


            public getRepleDataHttp(String loginUserId, String targetId, String content, int commentNo, int mode) {
                this.loginUserId = loginUserId;
                this.targetId = targetId;
                this.content = content;
                this.commentNo = commentNo;
                this.mode = mode;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("데이터 불러오는 중");
                dialog.setCanceledOnTouchOutside(false); // 바깥 터치 안되게
                dialog.setCancelable(false); // 뒤로가기로 캔슬시키는거 안되게
                dialog.show();
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    //--------------------------
                    //   URL 설정하고 접속하기
                    //--------------------------
                    URL url = new URL(ipad+"/editModeForRe_Recomment.php");
                    HttpURLConnection huc = (HttpURLConnection) url.openConnection();

                    //--------------------------
                    //   전송 모드 설정 - 기본적인 설정이다
                    //--------------------------

                    huc.setDefaultUseCaches(false);
                    huc.setDoInput(true);                         // 서버에서 읽기 모드 지정
                    huc.setDoOutput(true);                       // 서버로 쓰기 모드 지정
                    huc.setRequestMethod("POST");         // 전송 방식은 POST

                    huc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    // 서버로 값 전달.
                    //--------------------------
                    //   서버로 값 전송
                    //--------------------------

                    // php 변수에 값 대입

                    StringBuffer buffer = new StringBuffer();
                    buffer.append("senderId").append("=").append(loginUserId).append("&");
                    buffer.append("targetId").append("=").append(targetId).append("&");
                    buffer.append("content").append("=").append(content).append("&");
                    buffer.append("commentNo").append("=").append(commentNo).append("&");
                    buffer.append("mode").append("=").append(mode).append("&");


                    OutputStreamWriter outStream = new OutputStreamWriter(huc.getOutputStream(), "UTF-8");  // 안드에서 php로 보낼때 UTF8로 해야지 한글이 안깨진다.
                    PrintWriter writer = new PrintWriter(outStream);
                    writer.write(buffer.toString());
                    writer.flush();


                    //--------------------------
                    //   서버에서 전송받기
                    //--------------------------
                    InputStreamReader tmp = new InputStreamReader(huc.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuilder builder = new StringBuilder();
                    String str;
                    while ((str = reader.readLine()) != null) {       // 서버에서 라인단위로 보내줄 것이므로 라인단위로 읽는다
                        builder.append(str);                     // View에 표시하기 위해 라인 구분자 추가
                    }
                    result2 = builder.toString();                       // 전송결과를 전역 변수에 저장

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return result2;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    if ( dialog != null && dialog.isShowing()){
                        dialog.dismiss();
                    }
                }catch (Exception e){
                }

                Log.d("대댓글 처리 상태",s);
                if(s.equals("댓글 수정")){
                    Toast.makeText(context, "댓글 수정 완료", Toast.LENGTH_SHORT).show();
                }else if (s.equals("댓글 삭제")){
                    Toast.makeText(context, "댓글 삭제 완료", Toast.LENGTH_SHORT).show();
                }

            }
        }
        getRepleDataHttp getData = new getRepleDataHttp(loginUserId,targetId,content,commentNo,mode);
        getData.execute();
    }

}
