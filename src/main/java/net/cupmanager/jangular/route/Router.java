package net.cupmanager.jangular.route;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.cupmanager.jangular.Scope;
import net.cupmanager.jangular.compiler.CompiledTemplate;
import net.cupmanager.jangular.compiler.CompilerConfiguration;
import net.cupmanager.jangular.compiler.QueueResourceSpecification;
import net.cupmanager.jangular.compiler.ResourceSpecification;
import net.cupmanager.jangular.compiler.TemplateCompiler;
import net.cupmanager.jangular.compiler.templateloader.TemplateLoader;
import net.cupmanager.jangular.injection.EvaluationContext;

import com.google.common.base.Function;


public class Router<R extends Route<R>, U> implements RouteRewriter<R, U>, RouteAuthenticator<R, U> {
	
	private Function<Route, R> routeConstructor;
	private List<RouteRewriter<R,U>> rewriters = new ArrayList<RouteRewriter<R,U>>();
	private RouteAuthenticator<R, U> authenticator = new RouteAuthenticator<R,U>() {
		@Override
		public boolean isAllowed(R route, U user) {
			return true;
		}
	};
	
	private TemplateCompiler compiler;
	private TemplateLoader<String> loader;
	private Router<R,U> router;
	private Function<HttpServletRequest,U> usupplier;
	
	private Class<? extends Scope> appScopeClass;
	
	private EvaluationContextSupplier<R, U> evalContextSupplier;
	private EvaluationScopeSupplier<R, U> evalScopeSupplier;
	
	
	
	
	public Router(Function<Route,R> routeConstructor, 
			CompilerConfiguration conf, 
			Function<HttpServletRequest,U> usupplier, 
			Class<? extends Scope> appScopeClass, 
			EvaluationContextSupplier<R, U> evalContextSupplier, 
			EvaluationScopeSupplier<R, U> evalScopeSupplier) {
		this.routeConstructor = routeConstructor;
		
		loader = conf.getDirectiveTemplateLoader();
		conf.getRepo().register(RoutedViewDirective.class);
		this.compiler = TemplateCompiler.Builder.create(conf);
		
		this.usupplier = usupplier;
		this.appScopeClass = appScopeClass;
		this.evalContextSupplier = evalContextSupplier;
		this.evalScopeSupplier = evalScopeSupplier;
	}
	
	
	
	
	public void addRewriter(RouteRewriter<R,U> rewriter) {
		rewriters.add(rewriter);
	}
	@Override
	public R rewrite(R route, U user) {
		for (RouteRewriter<R,U> rw : rewriters) {
			route = rw.rewrite(route, user);
		}
		return route;
	}
	

	@Override
	public boolean isAllowed(R route, U user) {
		return authenticator.isAllowed(route,user);
	}

	public void setAuthenticator(RouteAuthenticator<R,U> authenticator) {
		this.authenticator = authenticator;
	}

	
	public R from(String path) {
		return routeConstructor.apply(new RouteImpl(path));
	}
	public R from(String target, String queryString) {
		String path = target;
		if (queryString != null) {
			path += "?" + queryString;
		}
		return from(path);
	}
	
	public ResourceSpecification route(String path, TemplateLoader<String> loader) {
		String[] parts = path.split("/");
		
		List<String> spec = new ArrayList<String>();
		
		String accumulatedPath = "";
		
		for (int i=0; i<parts.length; i++) {
			if (i==0) {
				spec.add("index.html");
			} else {
				accumulatedPath += "/" + parts[i];
				boolean exists = loader.exists(accumulatedPath+".html");
				if (exists) {
					spec.add(accumulatedPath+".html");
				} else {
					spec.add(accumulatedPath+"/"+parts[i]+".html");
				}
			}
		}
		if (spec.isEmpty()) {
			spec.add("index.html");
		}
		return new QueueResourceSpecification(spec);
	}
	
	public boolean handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) throws IOException, ServletException {
		try {
			
			String[] ignore = new String[]{"^/assets", "^/favicon"};
//			String[] open = new String[]{"^/$", "^/files", "^/login"};
			
			boolean shouldIgnore = RouteUtil.matchesAny(target, ignore);
			if (!shouldIgnore) {
//				boolean isOpen = matchesAny(target, open);
				
				U user = usupplier.apply(request);   
				R route = from(target, request.getQueryString());
				
				R newRoute = rewrite(route, user);
				boolean allowed = isAllowed(newRoute, user);
				if (!allowed) {
					
					response.setCharacterEncoding("UTF-8");
					response.setHeader("Content-Type", "text/html; charset=UTF-8");
					
					OutputStreamWriter ow = new OutputStreamWriter(response.getOutputStream(), "UTF-8");
					ow.write("Denna del av sidan har du inte tillg&aring;ng till.<br/>" +
							"<a href='/'>Gå till startsidan</a>");
					ow.flush();
					return true;
				} else {
					if (!newRoute.equals(route)) {
						response.sendRedirect(newRoute.toString());
						return true;
					} else {
						ResourceSpecification spec = route(target, loader);
						
						CompiledTemplate template = compiler.compile(spec, appScopeClass);
						template.printWarnings();
						
						EvaluationContext context = evalContextSupplier.supply(route, user);
//						JangularHandler.NyckeltalEvalContext context = new NyckeltalEvalContext();
//						context.user = user;
//						context.route = route;
						
						Scope scope = evalScopeSupplier.supply(route, user);
//						JangularHandler.NyckeltalAppScope appscope = new NyckeltalAppScope();
//						appscope.Route = route;
//						appscope.isOpen = isOpen;
						
						StringBuilder sb = new StringBuilder();
						long start = System.currentTimeMillis();
						template.eval(scope, sb, context);
						long end = System.currentTimeMillis();
						System.out.println("Eval took: " + (end-start)+ " ms");
						
						
						
						response.setCharacterEncoding("UTF-8");
						response.setHeader("Content-Type", "text/html; charset=UTF-8");
						
						OutputStreamWriter ow = new OutputStreamWriter(response.getOutputStream(), "UTF-8");
						ow.write(sb.toString());
						ow.flush();
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	
	
	

	
	
}
