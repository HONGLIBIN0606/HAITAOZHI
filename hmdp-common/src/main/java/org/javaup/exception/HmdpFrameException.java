package org.javaup.exception;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.javaup.enums.BaseCode;

/**
 * @program: 极度真实还原大麦网高并发实战项目。 添加 阿星不是程序员 微信，添加时备注 大麦 来获取项目的完整资料 
 * @description: 业务异常
 * @author: 阿星不是程序员
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class HmdpFrameException extends BaseException {
	
	private String message;

	public HmdpFrameException() {
		super();
	}

	public HmdpFrameException(String message) {
		super(message);
	}
	
	public HmdpFrameException(Integer code, String message) {
		super(message);
		this.message = message;
	}
	
	public HmdpFrameException(BaseCode baseCode) {
		super(baseCode.getMsg());
		this.message = baseCode.getMsg();
	}

	public HmdpFrameException(Throwable cause) {
		super(cause);
	}

	public HmdpFrameException(String message, Throwable cause) {
		super(message, cause);
		this.message = message;
	}
}
