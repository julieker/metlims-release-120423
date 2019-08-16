package edu.umich.brcf.metabolomics.panels.lims.newestprep.protocol;
 
import org.apache.wicket.IRequestListener;
import org.apache.wicket.Page;
import org.apache.wicket.RequestListenerInterface;
import org.apache.wicket.core.request.handler.BookmarkableListenerRequestHandler;
import org.apache.wicket.core.request.handler.ListenerRequestHandler;
import org.apache.wicket.core.request.handler.PageAndComponentProvider;
// issue 358 
import org.apache.wicket.IRequestListener;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.IResource.Attributes;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.request.resource.ResourceStreamResource;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.string.Strings;

/**
 * Implementation of an <a href="http://www.w3.org/TR/REC-html40/present/frames.html#h-16.5">inline
 * frame</a> component. Must be used with an iframe (&lt;iframe src...) element. The src attribute
 * will be generated. Its is suitable for displaying <em>generated contend</em> like PDF, EXCEL, WORD, 
 * etc.
 * 
 * @author Ernesto Reinaldo Barreiro
 * 
 */

// JAK issue 170 display pdf in iframe
public class DocumentInlineFrame extends WebMarkupContainer implements IRequestListener // Issue 464
{
	private static final long serialVersionUID = 1L;
	public static final RequestListenerInterface INTERFACE = new RequestListenerInterface((Class<? extends IRequestListener>) IRequestListener.class).setIncludeRenderCount(false).setRenderPageAfterInvocation(false);
	private IResource documentResource;
	private boolean firstTimeRendering = true;
	/**
	 * 
	 * Constructor receiving an IResourceStream.
	 * 
	 * @param id
	 * @param stream
	 */
	public DocumentInlineFrame(String id, IResourceStream stream) 
	    {
		this(id, new ResourceStreamResource(stream));		
	    }
	
	/**
	 * Constructor receiving an IResource..
	 * 
	 * @param id
	 * @param resourceListener
	 */
	public DocumentInlineFrame(final String id, IResource documentResource)
	    {
		super(id);
		this.documentResource = documentResource;
	    }

	/**
	 * Gets the url to use for this link.
	 * 
	 * @return The URL that this link links to
	 */
	
	protected CharSequence getURL()
	    {
		PageParameters pageParameters = new PageParameters(); 	
		pageParameters.set("id", Math.random());
		String iArg = "i="+ Math.random();		
		Object pageParametersClass;
		return urlFor( INTERFACE, pageParameters);
	    }

	/**
	 * Handles this frame's tag.
	 * 
	 * @param tag
	 *            the component tag
	 * @see org.apache.wicket.Component#onComponentTag(ComponentTag)
	 */
	@Override
	protected final void onComponentTag(final ComponentTag tag)
	    {
			checkComponentTag(tag, "iframe");
		    CharSequence url = getURL();
		    url = url.toString() + "&ij=" + Math.random();
			tag.put("src", Strings.replaceAll(Strings.replaceAll(url, "&", "&amp;"), "--", "-IResourceListener-"));
			super.onComponentTag(tag);
	    }

	@Override
	protected boolean getStatelessHint()
	    {	
		return false;
	    }


	@Override
	public void onRequest() 
	    {
			RequestCycle requestCycle = RequestCycle.get();
			Attributes attributes = new Attributes(requestCycle.getRequest(),
				requestCycle.getResponse(), null);
			this.documentResource.respond(attributes);
			//return;
	
	    }
	
	// issue 464
	@Override
	public boolean rendersPage()
		{
		 if (firstTimeRendering)
			{
			firstTimeRendering = false;
			return true;
			}
		 else
			return false;
		}

	// issue 464
	public final CharSequence urlFor(final RequestListenerInterface listener,
			final PageParameters parameters)
		{
			IRequestHandler handler = createRequestHandler(listener, parameters, null);
			return getRequestCycle().urlFor(handler);
		}
	
	private IRequestHandler createRequestHandler(RequestListenerInterface listener,
			PageParameters parameters, Integer id)
		{
			Page page = getPage();
			PageAndComponentProvider provider = new PageAndComponentProvider(page, this, parameters);
			if (page.isPageStateless()
				|| (getApplication().getPageSettings().getRecreateBookmarkablePagesAfterExpiry()
					&& page.isBookmarkable() && page.wasCreatedBookmarkable()))
				return new BookmarkableListenerRequestHandler(provider);
			else
				return new ListenerRequestHandler(provider, id); // issue 464
		}
	
}