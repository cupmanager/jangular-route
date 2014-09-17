package net.cupmanager.jangular.route;

import java.io.InputStream;

import net.cupmanager.jangular.AbstractDirective;
import net.cupmanager.jangular.Scope;
import net.cupmanager.jangular.annotations.Directive;
import net.cupmanager.jangular.annotations.Transparent;
import net.cupmanager.jangular.compiler.CompilerContext;
import net.cupmanager.jangular.compiler.templateloader.TemplateLoader;
import net.cupmanager.jangular.compiler.templateloader.TemplateLoaderException;
import net.cupmanager.jangular.nodes.JangularNode;

@Directive("routed-view")
@Transparent
public class RoutedViewDirective extends AbstractDirective<Scope> {
	private CompilerContext context;
	
	@Override
	public InputStream getDirectiveTemplateInputStream(TemplateLoader<String> loader) throws TemplateLoaderException {
		String resource = context.resourceSpecification.getRootResource();
		return resource==null ? null : loader.loadTemplate(resource);
	}
	
	@Override
	public CompilerContext preCompile(CompilerContext context, JangularNode content) {
		this.context = context.tail();
		return this.context;
	}
}