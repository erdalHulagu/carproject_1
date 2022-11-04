package com.visionrent.exception;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import com.visionrent.exception.message.ApiResponseError;
@ControllerAdvice // merkezi exception handle etmek için @ControllerAdvice ekledim butun Exceptionlar ilk bu classa gelecek 
                     //buraya kendi craete ettigim annotationlarida ekliycem. butun exeptionlar burada valide edilevek
public class VisionRentExceptionHandler  extends ResponseEntityExceptionHandler{ //bu hazir class ->extends ResponseEntityExceptionHandler
	                                                                             // burada herhangi biryerde ctrl bosluk ile butun exceptionlari gorebilirim
	// AMACIM : custom bir exception sistemini kurmak, gelebilecek exceptionları override ederek, istediğim yapıda
	// cevap verilmesini sağlıyorum
	
	
	// gelecek exceptionlari loglamak icin logger nesnesi olsuturduk ve getlogger ile bu icine bu classi yukledik
	Logger logger = LoggerFactory.getLogger(VisionRentExceptionHandler.class);
	
	
	/* Kullaniciya exception mesaji gondermek icin kburada ResponseEntity metodu olusturup 
	 daha evvel olusturdugumuz icinde mesaj icin exception fieldlari olan -->ApiResponseError clasiini icine yukledik 
	 boylece bu class icin merkezi mesaj metodumuz olusmus oldu buran sadece returne yaparak istedigimiz exception icin mesaj gonderebilecez*/ 
   private ResponseEntity<Object> buildResponseEntity(  ApiResponseError error) { 
	   logger.error(error.getMessage()); //  eception fırlarsa mesajını loggladık
	   return new ResponseEntity<>(error, error.getStatus()); 
	  
   }
	
	
	@ExceptionHandler(ResourceNotFoundException.class) // bu @ ile custom exceptionrımı yakalayacağım
	protected ResponseEntity<Object> handleResourceNotFoundException ( ResourceNotFoundException ex, WebRequest  request  )   {
																																																// WebRequest = gelen request
			ApiResponseError error =  new ApiResponseError( HttpStatus.NOT_FOUND, ex.getMessage(), request.getDescription(false)  );
	       
			/*
			 *  Map<String,String> map= new HashMap<>();
			 *  map.put("time", LocalDateTime.noe().toString());
		     *  map.put("message", ex.getMessage());
		     *  return new ResponseEntity<>(map,HttpStatus.CREATED);
			 */
				
			
			return buildResponseEntity(error);
			
	}
	
	/* buradan sonra Alt bolume kadar ctrl bosluk ile exceptionlara baktik en cok exceptionlarin olusacagi
       hazir metodlari burada override edilmis birsekilde sadece ApiResponseError clasimiz icin burada nesne olusturup 
	   kullaniciya icine istedigimiz fieldlarda mesajlar gormeyi setledik.
	*/
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		
		    List<String> errors = ex.getBindingResult().getFieldErrors(). // bütün field errorlarını get ile aldım
		    																			stream().
		    																			map(e->e.getDefaultMessage()).//bütün errorların getMessage() metodunu alıyorum
		    																			collect(Collectors.toList());
		   
		    ApiResponseError error = new  ApiResponseError(HttpStatus.BAD_REQUEST,
		    																				errors.get(0).toString(),
		    																				request.getDescription(false));
		   
		    return buildResponseEntity(error);
		    		
	
	}
	
	@Override
	protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		      ApiResponseError error = new  ApiResponseError(HttpStatus.BAD_REQUEST,
		    		  																					ex.getMessage(),
		    		  																				     request.getDescription(false));
		      return buildResponseEntity(error);
	}
	
	@Override
	protected ResponseEntity<Object> handleConversionNotSupported(ConversionNotSupportedException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		ApiResponseError error = new  ApiResponseError(HttpStatus.INTERNAL_SERVER_ERROR,
					ex.getMessage(),
				     request.getDescription(false));
        return buildResponseEntity(error);
	}
	
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		
		ApiResponseError error = new  ApiResponseError(HttpStatus.BAD_REQUEST,
				ex.getMessage(),
			     request.getDescription(false));
    return buildResponseEntity(error);
		
	}
	
	
	/* Alttaki iki exceptionuda spesific olarak handle edemedigimiz exceptionlar kesinlikle bu 
	 * ikisinden birine takilmasi icin olusturduk, en baba olan EXCEPTION classini burda cegirdik
	 *  ve istedigimiz mesajda handle ettik
	 */
	@ExceptionHandler(RuntimeException.class)
	protected ResponseEntity<Object> handleRuntimeException ( RuntimeException ex, WebRequest  request  )   {
		
		ApiResponseError error =  new ApiResponseError( HttpStatus.INTERNAL_SERVER_ERROR,
																									ex.getMessage(),
																									request.getDescription(false)  );
		return buildResponseEntity(error);
}
	
	@ExceptionHandler(Exception.class)
	protected ResponseEntity<Object> handleGeneralException ( Exception ex, WebRequest  request  )   {
		
		ApiResponseError error =  new ApiResponseError( HttpStatus.INTERNAL_SERVER_ERROR,
																								ex.getMessage(),
																								request.getDescription(false)  );
		return buildResponseEntity(error);
}
	
}