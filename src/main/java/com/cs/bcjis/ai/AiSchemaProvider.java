package com.cs.bcjis.ai;

import org.springframework.stereotype.Component;

/**
 * \uc2ec\uc0ac\uc815\ubcf4\uc2dc\uc2a4\ud15c(CUBRID) DB \uc2a4\ud0a4\ub9c8 \uc124\uba85.
 *
 * Gemini \uc758 \ud14d\uc2a4\ud2b8\u2192SQL \ubcc0\ud658\uc744 \uc704\ud574 \uc81c\uacf5\ud558\ub294 \ub0b4\ubd80 \ub370\uc774\ud130 \uc2a4\ud0a4\ub9c8\uc774\ub2e4.
 * \uc2e4\uc81c \ud14c\uc774\ube14 \uad6c\uc870(\uc5b8\ub85c\ub4dc \ud30c\uc77c %class \ud5e4\ub354)\uc5d0\uc11c \ucd94\ucd9c\ud55c \uceec\ub7fc \ubaa9\ub85d\uacfc
 * \uc5c5\ubb34 \uc758\ubbf8(\ud55c\uae00 \uc124\uba85)\uc744 \ud3ec\ud568\ud55c\ub2e4.
 */
@Component("aiSchemaProvider")
public class AiSchemaProvider {

    public String getSchemaText() {
        StringBuilder sb = new StringBuilder();

        sb.append("[\uc2ec\uc0ac\uc815\ubcf4\uc2dc\uc2a4\ud15c(\uc608\uc0b0\ud3b8\uc131) CUBRID \ub370\uc774\ud130\ubca0\uc774\uc2a4 \uc2a4\ud0a4\ub9c8]\n");
        sb.append("- \ubaa8\ub4e0 \uae08\uc561 \uceec\ub7fc \ub2e8\uc704\ub294 '\ucc9c\uc6d0' \uc774\ub2e4.\n");
        sb.append("- fis_year=\ud68c\uacc4\uc5f0\ub3c4(\uc608:'2024'), bgt_dgr=\uc608\uc0b0\ucc28\uc218(\uc22b\uc790).\n");
        sb.append("- \ucf54\ub4dc\uac12\uc758 \ud55c\uae00\uba85\uc774 \ud544\uc694\ud558\uba74 TB_COMMCDDETL(cl_cd=\ubd84\ub958\ucf54\ub4dc, detl_cd=\uc0c1\uc138\ucf54\ub4dc, detl_cd_nm=\ucf54\ub4dc\uba85)\uacfc JOIN \ud55c\ub2e4.\n");
        sb.append("- report_cd \uc758\ubbf8: 010=\uacbd\uc0c1\uc0ac\uc5c5\uc2ec\uc0ac\uc870\uc11c, 020=\ud22c\uc790\uc0ac\uc5c5\uc2ec\uc0ac\uc870\uc11c, 040=\uae30\ubcf8\uacbd\ube44, 060=\uacf5\ud1b5\uacbd\ube44.\n");
        sb.append("\n");

        sb.append("\uD14C\uC774\uBE14 \uBAA9\uB85D:\n\n");

        sb.append("TB_FISYEAR (\ud68c\uacc4\uc5f0\ub3c4): fis_year(\ud68c\uacc4\uc5f0\ub3c4), year_desc(\uc5f0\ub3c4\uc124\uba85), regi_id, regi_date, modi_id, modi_date\n");

        sb.append("TB_BGTDGR (\uc608\uc0b0\ucc28\uc218): fis_year(\ud68c\uacc4\uc5f0\ub3c4), bgt_dgr(\uc608\uc0b0\ucc28\uc218), bgt_compo_fg(\ud3b8\uc131\uad6c\ubd84\ucf54\ub4dc), add_times(\ucd94\uacbd\ucc28\uc218), bgt_dgr_desc(\ucc28\uc218\uc124\uba85), pledge_info_id, regi_id, regi_date, modi_id, modi_date\n");

        sb.append("TB_DGRDEPT (\ucc28\uc218\ubcc4 \ubd80\uc11c): fis_year, bgt_dgr, dept_cd(\ubd80\uc11c\ucf54\ub4dc), bgt_compo_fg, add_times, bgt_bill_def_yn, gov_office_cd, office_rank, office_cd(\uc2e4\uad6d\ucf54\ub4dc), office_nm(\uc2e4\uad6d\uba85), dept_rank, dept_nm(\ubd80\uc11c\uba85), dept_fg, dept_fg_nm(\ubd80\uc11c\uad6c\ubd84\uba85), regi_id, regi_date, modi_id, modi_date\n");

        sb.append("TB_DGRBIZ (\ucc28\uc218\ubcc4 \uc0ac\uc5c5): fis_year, bgt_dgr, dbiz_cd(\uc138\ubd80\uc0ac\uc5c5\ucf54\ub4dc), dept_cd(\ubd80\uc11c\ucf54\ub4dc), fis_fg_mst_cd(\ud68c\uacc4\ub9c8\uc2a4\ud130\uad6c\ubd84\ucf54\ub4dc), fis_fg_mst_nm(\ud68c\uacc4\ub9c8\uc2a4\ud130\uad6c\ubd84\uba85), fis_fg_cd(\ud68c\uacc4\uad6c\ubd84\ucf54\ub4dc), fis_fg_nm(\ud68c\uacc4\uad6c\ubd84\uba85), sect_cd(\ubd84\uc57c\ucf54\ub4dc), sect_nm(\ubd84\uc57c\uba85), fld_cd(\ubd80\ubb38\ucf54\ub4dc), fld_nm(\ubd80\ubb38\uba85), pbiz_rank, pbiz_cd(\uc815\ucc45\uc0ac\uc5c5\ucf54\ub4dc), pbiz_nm(\uc815\ucc45\uc0ac\uc5c5\uba85), ubiz_rank, ubiz_cd(\ub2e8\uc704\uc0ac\uc5c5\ucf54\ub4dc), ubiz_nm(\ub2e8\uc704\uc0ac\uc5c5\uba85), dbiz_rank, dbiz_nm(\uc138\ubd80\uc0ac\uc5c5\uba85), pbiz_fg, pbiz_fg_nm, ubiz_fg, ubiz_fg_nm, dbiz_fg, dbiz_fg_nm, regi_id, regi_date, modi_id, modi_date\n");

        sb.append("TB_DGRTEMNGMOK (\ud1b5\uacc4\ubaa9): fis_year, bgt_dgr, te_mng_mok_cd(\ud1b5\uacc4\ubaa9\ucf54\ub4dc), te_mng_mok_nm(\ud1b5\uacc4\ubaa9\uba85), regi_id, regi_date, modi_id, modi_date, add_yn\n");

        sb.append("TB_DGRCOMPO (\uc608\uc0b0\ud3b8\uc131 \uc0b0\ucd9c\ub0b4\uc5ed/\uc2ec\uc0ac\uc870\uc11c \uad6c\uc131 - \ud575\uc2ec \ud14c\uc774\ube14): fis_year, bgt_dgr, te_bgt_compo_id(\ud3b8\uc131ID), dept_cd(\ubd80\uc11c\ucf54\ub4dc), dbiz_cd(\uc138\ubd80\uc0ac\uc5c5\ucf54\ub4dc), te_mng_mok_cd(\ud1b5\uacc4\ubaa9\ucf54\ub4dc), te_mng_mok_nm(\ud1b5\uacc4\ubaa9\uba85), te_bgt_compo_seq, demand_compo_sort_seq, demand_compo_level, demand_comp_ground(\uc694\uad6c\uc0b0\ucd9c\uadfc\uac70), demand_comp_formular(\uc694\uad6c\uc0b0\ucd9c\uc2dd), demand_bgt_amt(\uc694\uad6c\uc608\uc0b0\uc561), demand_diff_amt(\uc694\uad6c\uc99d\uac10\uc561), demand_compo_level_amt, demand_form_dsp_yn, compo_sort_seq, compo_level(\uad6c\uc131\ub808\ubca8), comp_ground(\uc0b0\ucd9c\uadfc\uac70), comp_formular(\uc0b0\ucd9c\uc2dd), bgt_amt(\uc608\uc0b0\uc561/\uc870\uc815\uc561), pre_formular, pre_amt(\uae30\uc815\uc561/\uc804\ub144\ub3c4\uc561), diff_amt(\uc99d\uac10\uc561), compo_level_amt, form_dsp_yn, up_te_bgt_compo_seq, up_te_bgt_compo_id, sort_seq, pre_comp_formular, pre_bgt_amt, cng_type, grp_id, grp_lvl, regi_id, regi_date, modi_id, modi_date, srch_val\n");

        sb.append("TB_REPORT010 (\uacbd\uc0c1\uc0ac\uc5c5 \uc2ec\uc0ac\uc870\uc11c): report_cd, report_detl_cd, fis_year, bgt_dgr, te_bgt_compo_id, te_bgt_compo_seq, reflect_fg(\ubc18\uc601\uad6c\ubd84), demand_cont(\uc694\uad6c\ub0b4\uc6a9), exam_cont(\uc2ec\uc0ac\ub0b4\uc6a9), srch_val, invest_plan, regi_id, regi_date, modi_id, modi_date\n");

        sb.append("TB_REPORT020 (\ud22c\uc790\uc0ac\uc5c5 \uc2ec\uc0ac\uc870\uc11c): report_cd, report_detl_cd, fis_year, bgt_dgr, te_bgt_compo_id, te_bgt_compo_seq, reflect_fg, demand_cont(\uc694\uad6c\ub0b4\uc6a9), exam_cont(\uc2ec\uc0ac\ub0b4\uc6a9), srch_val, report_sort_seq, tot_frsc_amt1~6(\uc7ac\uc6d0\ubcc4 \ucd1d\uc561: 1\uad6d\ube44 2\uc2dc\ub3c4\ube44 3\uc2dc\uad70\uad6c\ube44 \ub4f1), tot_char_amt1~3, pre_inv_frsc_amt1~6, pre_inv_char_amt1~3, mayor_report_yn, invest_plan, regi_id, regi_date, modi_id, modi_date\n");

        sb.append("TB_REPORT010_H / TB_REPORT020_H (\uc2ec\uc0ac\uc870\uc11c \uc774\ub825): \uc704 \uc870\uc11c\uc758 \ubcc0\uacbd \uc774\ub825. his_seq(\uc774\ub825\uc21c\ubc88), his_fg(\uc774\ub825\uad6c\ubd84) \ucd94\uac00.\n");

        sb.append("TB_REPORT070_O (\uc9c0\uc2dc\uc77c\uc790): fis_year, bgt_dgr, order_ymd_seq, order_ymd(\uc9c0\uc2dc\uc77c\uc790), del_yn, regi_id, regi_date, modi_id, modi_date\n");

        sb.append("TB_COMMCD (\uacf5\ud1b5\ucf54\ub4dc \uadf8\ub8f9): cl_cd(\ubd84\ub958\ucf54\ub4dc), cl_cd_nm(\ubd84\ub958\uba85), cd_descr, mng_subj, distr_yn, use_yn, regi_id, regi_date, modi_id, modi_date\n");

        sb.append("TB_COMMCDDETL (\uacf5\ud1b5\ucf54\ub4dc \uc0c1\uc138): cl_cd(\ubd84\ub958\ucf54\ub4dc), detl_cd(\uc0c1\uc138\ucf54\ub4dc), group_id, detl_cd_nm(\ucf54\ub4dc\uba85), default_val_yn, line_up_ord, mng_item_val, use_yn, regi_id, regi_date, modi_id, modi_date\n");

        sb.append("TB_YEARFISFG (\uc5f0\ub3c4\ubcc4 \ud68c\uacc4\uad6c\ubd84): fis_year, fis_fg_cd(\ud68c\uacc4\uad6c\ubd84\ucf54\ub4dc), fis_fg_nm(\ud68c\uacc4\uad6c\ubd84\uba85), fis_fg_mst_cd, fis_fg_mst_nm, regi_id, regi_date, modi_id, modi_date\n");

        sb.append("TB_YEARFRSC (\uc5f0\ub3c4\ubcc4 \uc7ac\uc6d0\uad6c\ubd84): fis_year, frsc_fg_cd(\uc7ac\uc6d0\uad6c\ubd84\ucf54\ub4dc), frsc_fg_nm(\uc7ac\uc6d0\uad6c\ubd84\uba85), frsc_fg_short_nm, stand_frsc_cd, stand_frsc_nm, regi_id, regi_date, modi_id, modi_date\n");

        sb.append("TB_TRACELOG (\uc811\uc18d\ub85c\uadf8): log_id, url, user_id, session_id, req_param, regi_year, regi_month, regi_day, regi_week, regi_date\n");

        return sb.toString();
    }
}
