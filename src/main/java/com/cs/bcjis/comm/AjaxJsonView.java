package com.cs.bcjis.comm;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.AbstractView;

import com.cs.bcjis.comm.util.BcjisCommUtil;

public class AjaxJsonView extends AbstractView{

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		response.setHeader("Content-Type", "text/html;charset=utf-8");
	    PrintWriter out = new PrintWriter(new OutputStreamWriter(response.getOutputStream(),"UTF-8"));
	       
	    out.print(model.get(BcjisCommUtil.JSON_OBJCT_NM));
	    out.flush();
	}
}
