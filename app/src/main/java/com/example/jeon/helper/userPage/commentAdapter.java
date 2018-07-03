package com.example.jeon.helper.userPage;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
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

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.support.v4.content.ContextCompat.startActivity;

/**
 * Created by JEON on 2018-05-24.
 */

public class commentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // 댓글 데이터
    ArrayList<commentData> commentArr = new ArrayList<>();
    Context context;
    String loginUserId;  // 접속한 아이디
    String targetId;  // 댓글의 타겟이 되는 아이디

    String reComment;


    com.example.jeon.helper.ip ip = new ip();
    String ipad = ip.getIp();


    public commentAdapter(ArrayList<commentData> commentArr, Context context, String loginUserId, String targetId) {
        this.commentArr = commentArr;
        this.context = context;
        this.loginUserId = loginUserId;
        this.targetId = targetId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 아이템을 디자인한 레이아웃을 불러오는 코드
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.userpage_reple_item, parent, false);
        return new RowCell(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        // 댓글 모드
        if (commentArr.get(position).mode.equals("0")) {

            //이미지 처리
            if (commentArr.get(position).makeProfile.equals("이미지 없음")) {
                Glide.with(context).load(R.drawable.kakao_default_profile_image).into(((RowCell) holder).profileImageView);
            } else {
                if (commentArr.get(position).makeProfile.contains("http://k.kakaocdn.net")) {
                    Glide.with(context).load(commentArr.get(position).makeProfile).into(((RowCell) holder).profileImageView);
                } else {
                    Glide.with(context).load(ipad + "/" + commentArr.get(position).makeProfile).into(((RowCell) holder).profileImageView);
                }
            }


            // 닉네임 날짜 내용 댓글 갯수
            ((RowCell) holder).makeNick.setText(commentArr.get(position).makeNick);
            ((RowCell) holder).makeDate.setText(commentArr.get(position).makeDate);
            ((RowCell) holder).makeContent.setText(commentArr.get(position).makeContent);

            // 삭제된 글일경우 작성자 닉네임 숨김
            if (commentArr.get(position).makeContent.equals("삭제된 글입니다.")) {
                ((RowCell) holder).makeNick.setText("");
                Glide.with(context).load(R.drawable.kakao_default_profile_image).into(((RowCell) holder).profileImageView);
            }


            // 댓글 갯수 이벤트  갯수가 있으면 리스트 이미지와 카운트 증가
            if (commentArr.get(position).count == 0) {
                ((RowCell) holder).comment_show_list.setVisibility(View.VISIBLE);
                ((RowCell) holder).comment_show_list.setVisibility(View.GONE);
            } else {
                ((RowCell) holder).comment_show_list.setVisibility(View.VISIBLE);
                ((RowCell) holder).re_comment_Count.setText("(" + commentArr.get(position).count + ")"); // 댓글수
            }


            // 접속자에 따른 버튼 숨기기 등.
            if (!loginUserId.equals(commentArr.get(position).makeId)) {
                // 다른인간
                ((RowCell) holder).commentEdit.setVisibility(View.VISIBLE);
                ((RowCell) holder).commentEdit.setVisibility(View.GONE);

                ((RowCell) holder).commentDelete.setVisibility(View.VISIBLE);
                ((RowCell) holder).commentDelete.setVisibility(View.GONE);

                ((RowCell) holder).commentComment.setVisibility(View.VISIBLE);

            } else if (loginUserId.equals(commentArr.get(position).makeId)) { // 같은인간
                ((RowCell) holder).commentEdit.setVisibility(View.VISIBLE);
                ((RowCell) holder).commentDelete.setVisibility(View.VISIBLE);
                ((RowCell) holder).commentComment.setVisibility(View.VISIBLE);
            }

        }

        // 답글 클릭 이벤트
        setRecommentBtn(position, holder);

        // 수정 클릭 이벤트
        setEditComment(position, holder);

        // 삭제 클릭 이벤트
        setDeletComment(position, holder);

        // 유저 보기
        gotoShowUserPage(position, holder);

        // 댓글 더보기 이벤트
        show_recomment_list(position, holder);

    }

    @Override
    public int getItemCount() {
        return commentArr.size();
    }

    private static class RowCell extends RecyclerView.ViewHolder {

        public ImageView profileImageView, comment_show_list;
        public TextView makeNick, makeDate, makeContent, commentEdit, commentDelete, commentComment, re_comment_Count;
        public EditText make_comment_comment;

        public LinearLayout recommentLayout, edit_recommentLayout;

        public TextView make_re_commentBtn, re_comment_cancel, edit_comment_comment, edit_comment_cancel, edit_re_commentBtn;


        public RowCell(View view) {
            super(view);
            profileImageView = (ImageView) view.findViewById(R.id.comment_user_profile);
            comment_show_list = (ImageView) view.findViewById(R.id.comment_show_list);


            makeNick = (TextView) view.findViewById(R.id.comment_user_nickName);
            makeDate = (TextView) view.findViewById(R.id.comment_date);
            makeContent = (TextView) view.findViewById(R.id.comment_content);
            commentEdit = (TextView) view.findViewById(R.id.comment_edit);
            commentDelete = (TextView) view.findViewById(R.id.comment_delete);
            commentComment = (TextView) view.findViewById(R.id.comment_comment);
            re_comment_Count = (TextView) view.findViewById(R.id.re_comment_Count);

            make_comment_comment = (EditText) view.findViewById(R.id.make_comment_comment);

            // 등록하기
            recommentLayout = (LinearLayout) view.findViewById(R.id.recommentLayout);
            make_re_commentBtn = (TextView) view.findViewById(R.id.make_re_commentBtn);
            re_comment_cancel = (TextView) view.findViewById(R.id.re_comment_cancel);

            // 수정하기
            edit_recommentLayout = (LinearLayout) view.findViewById(R.id.edit_recommentLayout);
            edit_comment_comment = (TextView) view.findViewById(R.id.edit_comment_comment);
            edit_comment_cancel = (TextView) view.findViewById(R.id.edit_comment_cancel);
            edit_re_commentBtn = (TextView) view.findViewById(R.id.edit_re_commentBtn);

        }
    }


    // 답글 클릭 이벤트
    public void setRecommentBtn(final int position, final RecyclerView.ViewHolder holder) {

        // 답글 버튼
        ((RowCell) holder).commentComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((RowCell) holder).recommentLayout.setVisibility(View.VISIBLE);
                ((RowCell) holder).make_comment_comment.setHint(commentArr.get(position).makeContent);
            }
        });

        // 취소버튼
        ((RowCell) holder).re_comment_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((RowCell) holder).recommentLayout.setVisibility(View.VISIBLE);
                ((RowCell) holder).recommentLayout.setVisibility(View.GONE);
            }
        });

        // 등록버튼  ( 모드 1 )  2는 수정 3은 삭제
        ((RowCell) holder).make_re_commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                reComment = ((RowCell) holder).make_comment_comment.getText().toString();

                // 서버 전송
                setCommentHttp(1, commentArr.get(position).no, loginUserId, targetId, reComment, context);
                commentArr.get(position).count = commentArr.get(position).count + 1;

                ((RowCell) holder).re_comment_Count.setText("(" + commentArr.get(position).count + ")"); // 댓글수

                if (commentArr.get(position).count > 0) {
                    ((RowCell) holder).comment_show_list.setVisibility(View.VISIBLE);
                }

                // 답글 작성하던 내용 비워버리기
                ((RowCell) holder).edit_comment_comment.setText(null);

                // 답글창 사라지게하기
                ((RowCell) holder).recommentLayout.setVisibility(View.VISIBLE);
                ((RowCell) holder).recommentLayout.setVisibility(View.GONE);
                reComment = null;

            }
        });
    }

    // 댓글 수정 이벤트
    public void setEditComment(final int position, final RecyclerView.ViewHolder holder) {

        // 수정 버튼
        ((RowCell) holder).commentEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //레이아웃 보여주기
                ((RowCell) holder).edit_recommentLayout.setVisibility(View.VISIBLE);
                //힌트 뿌려주기
                ((RowCell) holder).edit_comment_comment.setText(commentArr.get(position).makeContent);
            }
        });

        // 수정 취소 버튼
        ((RowCell) holder).edit_comment_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((RowCell) holder).edit_comment_comment.setText(null);
                ((RowCell) holder).edit_recommentLayout.setVisibility(View.VISIBLE);
                ((RowCell) holder).edit_recommentLayout.setVisibility(View.GONE);
            }
        });

        // 수정 ( 변경 버튼 )
        ((RowCell) holder).edit_re_commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 입력한 글 받기
                reComment = null;
                reComment = ((RowCell) holder).edit_comment_comment.getText().toString();

                // 서버에전송 ( 모드 2 )
                setCommentHttp(2, commentArr.get(position).no, loginUserId, targetId, reComment, context);

                // 레이아웃 숨기기
                ((RowCell) holder).edit_recommentLayout.setVisibility(View.VISIBLE);
                ((RowCell) holder).edit_recommentLayout.setVisibility(View.GONE);

                // 글 제목 변경해주기
                commentArr.get(position).makeContent = reComment;
                ((RowCell) holder).makeContent.setText(commentArr.get(position).makeContent);

                // 글비워주기      // 값 null 해주기
                reComment = null;
                ((RowCell) holder).edit_comment_comment.setText(null);

            }
        });

    }

    // 댓글 삭제 이벤트
    public void setDeletComment(final int position, final RecyclerView.ViewHolder holder) {

        ((RowCell) holder).commentDelete.setOnClickListener(new View.OnClickListener() {
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
                        setCommentHttp(3, commentArr.get(position).no, "", targetId, "삭제된 글입니다.", context);
                        // 프로필 사진 없애고
                        commentArr.get(position).makeProfile = "이미지 없음";
                        // 작성자 없애고
                        commentArr.get(position).makeNick = "";
                        // 내용 변경
                        commentArr.get(position).makeContent = "삭제된 글입니다.";
                        notifyItemChanged(position);
                    }
                });

                ab.show();
            }
        });

        if (commentArr.get(position).makeContent.equals("삭제된 글입니다.")) {
            ((RowCell) holder).commentEdit.setVisibility(View.VISIBLE);
            ((RowCell) holder).commentEdit.setVisibility(View.GONE);

            ((RowCell) holder).commentDelete.setVisibility(View.VISIBLE);
            ((RowCell) holder).commentDelete.setVisibility(View.GONE);

            ((RowCell) holder).commentComment.setVisibility(View.VISIBLE);
        }
    }

    // ( 서버 전송 )  모드 1 등록 2 수정 3 삭제
    public void setCommentHttp(int editMode, String commentNumber, String makeUserId, String targetId, String content, final Context context) {
        class getRepleDataHttp extends AsyncTask<Void, Void, String> {

            String result2;

            ProgressDialog dialog = new ProgressDialog(context);

            int editMode;
            String makeUserId;
            String targetId;
            String content;
            String commentNumber;

            public getRepleDataHttp(int editMode, String makeUserId, String targetId, String content, String commentNumber) {
                this.editMode = editMode;
                this.makeUserId = makeUserId;
                this.targetId = targetId;
                this.content = content;
                this.commentNumber = commentNumber;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

                if (editMode == 1 || editMode == 2) {
                    dialog.setMessage("답글을 저장하는 중");
                } else {
                    dialog.setMessage("저장하는 중");
                }
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
                    URL url = new URL(ipad + "/editModeForReComment.php");
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


                    StringBuffer buffer = new StringBuffer();
                    buffer.append("editMode").append("=").append(editMode).append("&");                 // php 변수에 값 대입
                    buffer.append("makeUserId").append("=").append(makeUserId).append("&");
                    buffer.append("targetId").append("=").append(targetId).append("&");
                    buffer.append("content").append("=").append(content).append("&");
                    buffer.append("commentNumber").append("=").append(commentNumber);


                    OutputStreamWriter outStream = new OutputStreamWriter(huc.getOutputStream(), "UTF-8");  // 안드에서 php로 보낼때 UTF8로 해야지 한글이 안깨진다.
//                    OutputStreamWriter outStream = new OutputStreamWriter(huc.getOutputStream(), "EUC-KR");
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
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                } catch (Exception e) {

                }
                Log.d("답글 결과", "" + s);

                if (s.equals("답글등록")) {
                    Toast.makeText(context, "답글 등록", Toast.LENGTH_SHORT).show();
                } else if (s.equals("댓글 수정")) {
                    Toast.makeText(context, "댓글 수정", Toast.LENGTH_SHORT).show();
                } else if (s.equals("댓글 삭제")) {
                    Toast.makeText(context, "댓글 삭제", Toast.LENGTH_SHORT).show();
                }

            }
        }

        getRepleDataHttp getData = new getRepleDataHttp(editMode, makeUserId, targetId, content, commentNumber);
        getData.execute();

    }

    // 대댓글 보기 버튼 이벤트  ( 엑티비티 이동 )
    public void show_recomment_list(final int position, final RecyclerView.ViewHolder holder) {

        if (commentArr.get(position).count == 0) {
        } else {

            ((RowCell) holder).comment_show_list.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent gotoShowMoreRiple = new Intent(context, showMoreRiple.class);
                    gotoShowMoreRiple.putExtra("loginUserId", loginUserId);
                    gotoShowMoreRiple.putExtra("targetId", commentArr.get(position).makeId);
                    gotoShowMoreRiple.putExtra("commentNo", commentArr.get(position).no);
                    gotoShowMoreRiple.putExtra("perComment", commentArr.get(position));
                    ((userPageMainActivity) context).startActivity(gotoShowMoreRiple);
                }
            });
        }
    }

    // 프로필이미지 클릭 이벤트  ( 사용자의 정보를 보러 간다 )
    public void gotoShowUserPage(final int position, final RecyclerView.ViewHolder holder) {

        ((RowCell) holder).profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (commentArr.get(position).makeContent.equals("삭제된 글입니다.")) {
                    Toast.makeText(context, "작성자의 정보를 알수 없습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent gotoShowMoreRiple = new Intent(context, selectMenu.class);
                    gotoShowMoreRiple.putExtra("loginUserId", loginUserId);
                    gotoShowMoreRiple.putExtra("targetId", commentArr.get(position).makeId);  // 대상
                    ((userPageMainActivity) context).startActivity(gotoShowMoreRiple);
                }
            }
        });

    }


}
