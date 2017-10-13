/*
 * Copyright (c) 2002-2017, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.myportal.modules.myapps.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.myapps.business.MyApps;
import fr.paris.lutece.plugins.myapps.business.MyAppsUser;
import fr.paris.lutece.plugins.myapps.modules.database.business.MyAppsDatabase;
import fr.paris.lutece.plugins.myapps.modules.database.business.MyAppsDatabaseFilter;
import fr.paris.lutece.plugins.myapps.modules.database.business.MyAppsDatabaseUser;
import fr.paris.lutece.plugins.myapps.modules.database.service.MyAppsDatabasePlugin;
import fr.paris.lutece.plugins.myapps.modules.database.service.MyAppsDatabaseService;
import fr.paris.lutece.plugins.myapps.modules.database.utils.constants.MyAppsDatabaseConstants;
import fr.paris.lutece.plugins.myportal.modules.myapps.services.MyPortalMyAppsService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

/**
 * 
 * XPage for the widget MyPortalMyApps
 *
 */
@Controller( xpageName = "myportal-myapps" )
public class MyPortalMyAppsXPage extends MVCApplication
{

    /**
     * Generated serial ID
     */
    private static final long serialVersionUID = 5491988303257261581L;

    // Templates
    private static final String TEMPLATE_MANAGE_USER_MYAPPS = "/skin/plugins/myportal/modules/myapps/manage_user_myapps.html";
    private static final String TEMPLATE_INSERT_USER_MYAPPS = "/skin/plugins/myportal/modules/myapps/insert_user_myapps.html";
    private static final String TEMPLATE_MODIFY_USER_MYAPPS = "/skin/plugins/myportal/modules/myapps/modify_user_myapps.html";

    // Views
    private static final String VIEW_MANAGE_USER_MYAPPS = "manage_user_myapps";
    private static final String VIEW_INSERT_USER_MYAPPS = "insert_user_myapps";
    private static final String VIEW_MODIFY_USER_MYAPPS = "modify_user_myapps";
    private static final String VIEW_CONFIRM_REMOVE_USER_MYAPPS = "confirm_remove_user_myapps";

    // Actions
    private static final String ACTION_DO_INSERT = "do_insert_user_myapps";
    private static final String ACTION_DO_MODIFY_USER_MYAPPS = "do_modify_user_myapps";
    private static final String ACTION_DO_REMOVE_USER_MYAPPS = "do_remove_user_myapps";

    // Parameters
    private static final String PARAMETER_BACK = "back";
    private static final String PARAMETER_MYPORTAL_URL_RETURN = "myportal_url_return";
    private static final String PARAMETER_MYAPP_ID = "myapp_id";
    private static final String PARAMETER_USER_LOGIN = "user_login";
    private static final String PARAMETER_USER_PASSWORD = "user_password";
    private static final String PARAMETER_USER_EXTRA_DATA = "user_extra_data";
    private static final String PARAMETER_MYAPP_CODE_CATEGORY = "myapp_code_category";
    private static final String PARAMETER_FROM_WIDGET = "from_widget";
    private static final String PARAMETER_MYAPPS_ORDER = "application_order";

    // Marks
    private static final String MARK_MYAPP = "myapp";
    private static final String MARK_MYAPP_USER = "myapp_user";
    private static final String MARK_MYPORTAL_URL_RETURN = "myportal_url_return";
    private static final String MARK_ENABLED_MYAPPS_LIST = "enabled_myapps_list";
    private static final String MARK_DISABLED_MYAPPS_LIST = "disabled_myapps_list";
    private static final String MARK_MYAPP_CATEGORY = "myapp_category";
    private static final String MARK_USER_MYAPPS_ORDER_LIST = "myapps_order_list";
    private static final String MARK_MYAPPS_CURRENT_ORDER = "myapps_current_order";

    // Messages
    private static final String MESSAGE_ERROR = "module.myapps.database.message.error";
    private static final String MESSAGE_APPLICATION_ADDED = "module.myportal.myapps.message.applicationAdded";
    private static final String MESSAGE_CONFIRM_REMOVE_MYAPPS = "module.myportal.myapps.message.confirmRemoveMyApps";
    private static final String MESSAGE_MYAPPS_SUCCESS_MODIFY = "module.myportal.myapps.message.modificationMade";
    private static final String MESSAGE_MYAPPS_SUCCESS_REMOVE = "module.myportal.myapps.message.myAppsRemove";

    // Properties
    private static final String MANAGE_USER_MYAPPS_TITLE_PAGE = "module.myportal.myapps.manage_user_myapps.pageTitle";
    private static final String MANAGE_USER_MYAPPS_PATH_PAGE = "module.myportal.myapps.manage_user_myapps.pagePathLabel";
    private static final String INSERT_USER_MYAPPS_TITLE_PAGE = "module.myportal.myapps.insert_user_myapps.pageTitle";
    private static final String INSERT_USER_MYAPPS_PATH_PAGE = "module.myportal.myapps.insert_user_myapps.pagePathLabel";
    private static final String MODIFY_USER_MYAPPS_TITLE_PAGE = "module.myportal.myapps.modify_user_myapps.pageTitle";
    private static final String MODIFY_USER_MYAPPS_PATH_PAGE = "module.myportal.myapps.modify_user_myapps.pageTitle";

    // Constants
    private static final String SITE_URL = "jsp/site/";

    // Variables
    private List<Integer> _listUserApplicationOrder = new ArrayList<>( );
    private final MyPortalMyAppsService _myPortalMyAppsService = SpringContextService.getBean( MyPortalMyAppsService.BEAN_NAME );

    /**
     * The manage page for adding or removing applications of a user
     * 
     * @param request
     *            The HttpServletRequest
     * @return the XPage of the management of the user applications
     * @throws UserNotSignedException
     */
    @View( value = VIEW_MANAGE_USER_MYAPPS, defaultView = true )
    public XPage getManageUserMyApps( HttpServletRequest request ) throws UserNotSignedException
    {
        // Manage the request back url
        String strBackParameter = request.getParameter( PARAMETER_BACK );
        String strBaseUrl = AppPathService.getBaseUrl( request );
        String strMyPortalUrlReturn = request.getParameter( PARAMETER_MYPORTAL_URL_RETURN );
        if ( strBackParameter != null && StringUtils.isNotBlank( strMyPortalUrlReturn ) )
        {
            return redirect( request, strBaseUrl + strMyPortalUrlReturn );
        }

        LuteceUser user = getUser( request );
        XPage page = new XPage( );
        Plugin plugin = PluginService.getPlugin( MyAppsDatabasePlugin.PLUGIN_NAME );
        String strMyAppCategory = request.getParameter( PARAMETER_MYAPP_CODE_CATEGORY );

        // Retrieve the list of enabled and disabled applications
        MyAppsDatabaseFilter filter = new MyAppsDatabaseFilter( );
        filter.setUserName( user.getName( ) );
        filter.setCategory( strMyAppCategory );
        List<MyApps> listEnabledMyApps = _myPortalMyAppsService.getOrderedMyAppsList( user.getName( ) );
        List<MyApps> listDisabledMyApps = MyAppsDatabaseService.getInstance( ).selectMyAppsList( filter, plugin );
        listDisabledMyApps.removeAll( listEnabledMyApps );

        // Generate the model
        Map<String, Object> model = new HashMap<String, Object>( );
        fillCommons( model );
        model.put( MARK_ENABLED_MYAPPS_LIST, listEnabledMyApps );
        model.put( MARK_DISABLED_MYAPPS_LIST, listDisabledMyApps );
        model.put( MARK_MYAPP_CATEGORY, strMyAppCategory );
        model.put( MARK_MYPORTAL_URL_RETURN, strMyPortalUrlReturn );

        // Populate the template with the model and construct the XPage associated
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_USER_MYAPPS, request.getLocale( ), model );
        page.setContent( template.getHtml( ) );
        page.setTitle( I18nService.getLocalizedString( MANAGE_USER_MYAPPS_TITLE_PAGE, request.getLocale( ) ) );
        page.setPathLabel( I18nService.getLocalizedString( MANAGE_USER_MYAPPS_PATH_PAGE, request.getLocale( ) ) );

        // Return XPage
        return page;
    }

    /**
     * Return the view which allow a user to add favorites applications
     * 
     * @param request
     *            The httpServletRequest
     * @return the view which allow a user to add favorites applications
     * @throws UserNotSignedException
     * @throws SiteMessageException
     */
    @View( VIEW_INSERT_USER_MYAPPS )
    public XPage getInsertUserMyApps( HttpServletRequest request ) throws UserNotSignedException, SiteMessageException
    {
        XPage page = new XPage( );
        Plugin plugin = PluginService.getPlugin( MyAppsDatabasePlugin.PLUGIN_NAME );
        String strMyPortalUrlReturn = request.getParameter( PARAMETER_MYPORTAL_URL_RETURN );

        String strMyAppId = request.getParameter( PARAMETER_MYAPP_ID );
        String strMyAppCategory = request.getParameter( PARAMETER_MYAPP_CODE_CATEGORY );

        if ( StringUtils.isNotBlank( strMyAppId ) && StringUtils.isNumeric( strMyAppId ) )
        {
            // Retrieve the MyAppsDatabase associated to the application id
            int nMyAppId = Integer.parseInt( strMyAppId );
            MyAppsDatabase myApp = (MyAppsDatabase) MyAppsDatabaseService.getInstance( ).findByPrimaryKey( nMyAppId, plugin );

            if ( myApp != null )
            {
                // Retrieve the list of user applications
                String strUserName = getUser( request ).getName( );
                _listUserApplicationOrder = _myPortalMyAppsService.getOrderedMyAppsIdList( strUserName );

                // Generate the model
                Map<String, Object> model = new HashMap<String, Object>( );
                model.put( MARK_MYAPP, myApp );
                model.put( MARK_MYAPP_CATEGORY, strMyAppCategory );
                model.put( MARK_MYPORTAL_URL_RETURN, strMyPortalUrlReturn );
                model.put( MARK_USER_MYAPPS_ORDER_LIST, _myPortalMyAppsService.getUserListOrderForCreation( strUserName ) );

                // Populate the template with the model and construct the XPage
                HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_INSERT_USER_MYAPPS, request.getLocale( ), model );
                page.setContent( template.getHtml( ) );
                page.setTitle( I18nService.getLocalizedString( INSERT_USER_MYAPPS_TITLE_PAGE, request.getLocale( ) ) );
                page.setPathLabel( I18nService.getLocalizedString( INSERT_USER_MYAPPS_PATH_PAGE, request.getLocale( ) ) );

                // Return the XPage
                return page;
            }
            else
            {
                // The application is null
                SiteMessageService.setMessage( request, MESSAGE_ERROR, SiteMessage.TYPE_STOP );
            }
        }
        else
        {
            // The application id is incorrect
            SiteMessageService.setMessage( request, MESSAGE_ERROR, SiteMessage.TYPE_STOP );
        }

        // Redirect on the default page
        return redirectView( request, VIEW_MANAGE_USER_MYAPPS );
    }

    /**
     * Action which allow user to add applications
     * 
     * @param request
     *            The HttpServletRequest
     * @return redirect to the manage page of application favorites
     * @throws UserNotSignedException
     * @throws SiteMessageException
     */
    @Action( ACTION_DO_INSERT )
    public XPage doInsertUserMyApps( HttpServletRequest request ) throws UserNotSignedException, SiteMessageException
    {
        String strMyPortalUrlReturn = request.getParameter( PARAMETER_MYPORTAL_URL_RETURN );

        Map<String, String> model = new HashMap<String, String>( );
        model.put( MARK_MYPORTAL_URL_RETURN, strMyPortalUrlReturn );

        // Manage the return back
        String strBackParameter = request.getParameter( PARAMETER_BACK );
        if ( strBackParameter != null )
        {
            return redirect( request, VIEW_MANAGE_USER_MYAPPS, model );
        }

        // Add the application to the user
        LuteceUser luteceUser = getUser( request );
        Plugin plugin = PluginService.getPlugin( MyAppsDatabasePlugin.PLUGIN_NAME );
        MyAppsDatabaseUser myAppsUser = getMyAppsDatabaseUserInfo( request, luteceUser, plugin );

        if ( myAppsUser != null )
        {
            // Associate the application to the user
            MyAppsDatabaseService.getInstance( ).createMyAppUser( myAppsUser, plugin );

            // Manage new ordering
            List<Integer> listNewMyAppsOrder = _myPortalMyAppsService.manageMyAppsNewOrderList( _listUserApplicationOrder, myAppsUser );
            _myPortalMyAppsService.manageMyAppsReordering( listNewMyAppsOrder, luteceUser.getName( ) );

            // Reset the actual list order
            _listUserApplicationOrder = listNewMyAppsOrder;
        }

        // Redirect to the manage view
        addInfo( MESSAGE_APPLICATION_ADDED, request.getLocale( ) );
        return redirect( request, VIEW_MANAGE_USER_MYAPPS, model );
    }

    /**
     * Return the XPage associated to the modification of an application
     * 
     * @param request
     *            The HttpServletRequest
     * @return the XPage associated to the modification of an application
     * @throws UserNotSignedException
     * @throws SiteMessageException
     */
    @View( VIEW_MODIFY_USER_MYAPPS )
    public XPage getModifyUserMyApps( HttpServletRequest request ) throws UserNotSignedException, SiteMessageException
    {
        // Retrieve data from the request
        String strMyAppId = request.getParameter( MyAppsDatabaseConstants.PARAMETER_MYAPP_ID );
        String strMyAppCategory = request.getParameter( MyAppsDatabaseConstants.PARAMETER_MYAPP_CODE_CATEGORY );
        String strMyPortalUrlReturn = request.getParameter( PARAMETER_MYPORTAL_URL_RETURN );
        String strFromWidget = request.getParameter( PARAMETER_FROM_WIDGET );

        if ( StringUtils.isNotBlank( strMyAppId ) && StringUtils.isNumeric( strMyAppId ) )
        {
            String strUserName = getUser( request ).getName( );

            // Get the MyAppsUser associated to the application id
            int nMyAppId = Integer.parseInt( strMyAppId );
            Plugin plugin = PluginService.getPlugin( MyAppsDatabasePlugin.PLUGIN_NAME );
            MyApps myApp = MyAppsDatabaseService.getInstance( ).findByPrimaryKey( nMyAppId, plugin );
            MyAppsUser myAppUser = MyAppsDatabaseService.getInstance( ).getCredential( nMyAppId, strUserName, plugin );

            if ( ( myApp != null ) && ( myAppUser != null ) )
            {
                // Rebuild the order list
                _listUserApplicationOrder = _myPortalMyAppsService.getOrderedMyAppsIdList( strUserName );
                int nOrder = _listUserApplicationOrder.indexOf( nMyAppId ) + 1;

                // Generate the model
                Map<String, Object> model = new HashMap<String, Object>( );
                model.put( MARK_MYAPP, myApp );
                model.put( MARK_MYAPP_CATEGORY, strMyAppCategory );
                model.put( MARK_MYPORTAL_URL_RETURN, strMyPortalUrlReturn );
                model.put( MARK_MYAPP_USER, myAppUser );
                model.put( PARAMETER_FROM_WIDGET, strFromWidget );
                model.put( MARK_MYAPPS_CURRENT_ORDER, nOrder );
                model.put( MARK_USER_MYAPPS_ORDER_LIST, _myPortalMyAppsService.getUserListOrder( strUserName ) );

                // Populate the template with the model
                XPage page = new XPage( );
                HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_USER_MYAPPS, request.getLocale( ), model );
                page.setContent( template.getHtml( ) );
                page.setTitle( I18nService.getLocalizedString( MODIFY_USER_MYAPPS_TITLE_PAGE, request.getLocale( ) ) );
                page.setPathLabel( I18nService.getLocalizedString( MODIFY_USER_MYAPPS_PATH_PAGE, request.getLocale( ) ) );

                // Return the XPage
                return page;
            }
            else
            {
                // The MyAppsUser is null
                SiteMessageService.setMessage( request, MESSAGE_ERROR, SiteMessage.TYPE_STOP );
            }
        }
        else
        {
            // The application id is incorrect
            SiteMessageService.setMessage( request, MESSAGE_ERROR, SiteMessage.TYPE_STOP );
        }

        // Redirect to the default page
        return redirectView( request, VIEW_MANAGE_USER_MYAPPS );
    }

    /**
     * Modify the application with the data filled by the user
     * 
     * @param request
     *            The httpServletRequest
     * @return make the modification and return to the manage page
     * @throws UserNotSignedException
     * @throws SiteMessageException
     */
    @Action( ACTION_DO_MODIFY_USER_MYAPPS )
    public XPage doModifyUserMyApps( HttpServletRequest request ) throws SiteMessageException, UserNotSignedException
    {
        String strMyPortalUrlReturn = request.getParameter( PARAMETER_MYPORTAL_URL_RETURN );

        Map<String, String> model = new HashMap<String, String>( );
        model.put( MARK_MYPORTAL_URL_RETURN, strMyPortalUrlReturn );

        // Manage the return back
        String strBackParameter = request.getParameter( PARAMETER_BACK );
        if ( strBackParameter != null )
        {
            String strFromWidget = request.getParameter( PARAMETER_FROM_WIDGET );
            if ( StringUtils.isNotBlank( strFromWidget ) )
            {
                return redirect( request, AppPathService.getBaseUrl( request ) + strMyPortalUrlReturn );
            }
            return redirect( request, VIEW_MANAGE_USER_MYAPPS, model );
        }

        // Modify the application from the user data
        LuteceUser luteceUser = getUser( request );
        Plugin plugin = PluginService.getPlugin( MyAppsDatabasePlugin.PLUGIN_NAME );
        MyAppsDatabaseUser myAppsUser = getMyAppsDatabaseUserInfo( request, getUser( request ), plugin );

        if ( myAppsUser != null )
        {
            // Update the user application
            MyAppsDatabaseService.getInstance( ).updateMyAppUser( myAppsUser, plugin );

            // Manage new ordering
            List<Integer> listNewMyAppsOrder = _myPortalMyAppsService.manageMyAppsNewOrderList( _listUserApplicationOrder, myAppsUser );
            _myPortalMyAppsService.manageMyAppsReordering( listNewMyAppsOrder, luteceUser.getName( ) );

            // Reset the actual list order
            _listUserApplicationOrder = listNewMyAppsOrder;

            // Manage the redirection if we are coming from the widget
            String strFromWidget = request.getParameter( PARAMETER_FROM_WIDGET );
            if ( StringUtils.isNotBlank( strFromWidget ) )
            {
                return redirect( request, AppPathService.getBaseUrl( request ) + strMyPortalUrlReturn );
            }

            // Return on the managing page
            addInfo( MESSAGE_MYAPPS_SUCCESS_MODIFY, request.getLocale( ) );
            return redirect( request, VIEW_MANAGE_USER_MYAPPS, model );
        }

        // Redirect to the default page
        return redirect( request, VIEW_MANAGE_USER_MYAPPS, model );
    }

    /**
     * Return the XPage of the confirmation of the removing of an application
     * 
     * @param request
     *            The HttpServletRequest
     * @return the XPage of the confirmation of the removing of an application
     * @throws SiteMessageException
     */
    @View( VIEW_CONFIRM_REMOVE_USER_MYAPPS )
    public XPage getConfirmRemoveUserMyApps( HttpServletRequest request ) throws SiteMessageException
    {
        // Get data from request
        String strUrlReturn = request.getParameter( PARAMETER_MYPORTAL_URL_RETURN );
        String strIdMyApp = request.getParameter( PARAMETER_MYAPP_ID );
        String strFromWidget = request.getParameter( PARAMETER_FROM_WIDGET );

        // Construct the return url of the confirmation page
        UrlItem url = new UrlItem( SITE_URL + getActionUrl( ACTION_DO_REMOVE_USER_MYAPPS ) );
        url.addParameter( PARAMETER_MYAPP_ID, strIdMyApp );
        url.addParameter( PARAMETER_MYPORTAL_URL_RETURN, strUrlReturn );
        if ( StringUtils.isNotBlank( strFromWidget ) )
        {
            url.addParameter( PARAMETER_FROM_WIDGET, strFromWidget );
        }

        SiteMessageService.setMessage( request, MESSAGE_CONFIRM_REMOVE_MYAPPS, SiteMessage.TYPE_CONFIRMATION, url.getUrl( ), null );
        SiteMessage siteMessage = SiteMessageService.getMessage( request );

        // Redirect to the confirmation page
        return redirect( request, siteMessage.getText( request.getLocale( ) ) );
    }

    /**
     * Remove an application form the favorites applications list of the user
     * 
     * @param request
     *            The HttpServletRequest
     * @return the XPage of the managing of the applications
     * @throws UserNotSignedException
     */
    @Action( ACTION_DO_REMOVE_USER_MYAPPS )
    public XPage doRemoveUserMyApps( HttpServletRequest request ) throws UserNotSignedException
    {
        String strMyAppId = request.getParameter( PARAMETER_MYAPP_ID );
        String strMyPortalUrlReturn = request.getParameter( PARAMETER_MYPORTAL_URL_RETURN );

        Map<String, String> model = new HashMap<String, String>( );
        model.put( MARK_MYPORTAL_URL_RETURN, strMyPortalUrlReturn );

        if ( StringUtils.isNotBlank( strMyAppId ) && StringUtils.isNumeric( strMyAppId ) )
        {
            // Retrieve the ordered list of user applications
            LuteceUser luteceUser = getUser( request );
            _listUserApplicationOrder = _myPortalMyAppsService.getOrderedMyAppsIdList( luteceUser.getName( ) );

            // Remove the user application
            int nMyAppId = Integer.parseInt( strMyAppId );
            Plugin plugin = PluginService.getPlugin( MyAppsDatabasePlugin.PLUGIN_NAME );
            MyAppsDatabaseService.getInstance( ).removeMyAppUser( nMyAppId, luteceUser.getName( ), plugin );

            // Manage new ordering
            List<Integer> listNewMyAppsOrder = new ArrayList<>( _listUserApplicationOrder );
            listNewMyAppsOrder.remove( (Integer) nMyAppId );

            _myPortalMyAppsService.manageMyAppsReordering( listNewMyAppsOrder, luteceUser.getName( ) );

            // Reset the actual list order
            _listUserApplicationOrder = listNewMyAppsOrder;

            // Manage the redirection if we are coming from the widget
            String strFromWidget = request.getParameter( PARAMETER_FROM_WIDGET );
            if ( StringUtils.isNotBlank( strFromWidget ) )
            {
                return redirect( request, AppPathService.getBaseUrl( request ) + strMyPortalUrlReturn );
            }

            // Redirect on the managing page
            addInfo( MESSAGE_MYAPPS_SUCCESS_REMOVE, request.getLocale( ) );
            return redirect( request, VIEW_MANAGE_USER_MYAPPS, model );
        }

        return redirect( request, VIEW_MANAGE_USER_MYAPPS, model );
    }

    /**
     * Get the current user
     *
     * @param request
     *            {@link HttpServletRequest}
     * @return the current {@link LuteceUser}
     * @throws UserNotSignedException
     *             exception if the current user is not connected
     */
    private LuteceUser getUser( HttpServletRequest request ) throws UserNotSignedException
    {
        LuteceUser user = SecurityService.getInstance( ).getRemoteUser( request );

        if ( user == null )
        {
            throw new UserNotSignedException( );
        }

        return user;
    }

    /**
     * Get MyAppsDatabaseUser
     * 
     * @param request
     *            {@link HttpServletRequest}
     * @param user
     *            the {@link LuteceUser}
     * @param plugin
     *            {@link Plugin}
     * @return a {@link MyAppsDatabaseUser}
     * @throws SiteMessageException
     *             exception if some parameters are not correctly filled
     */
    private MyAppsDatabaseUser getMyAppsDatabaseUserInfo( HttpServletRequest request, LuteceUser user, Plugin plugin ) throws SiteMessageException
    {
        MyAppsDatabaseUser myAppsUser = null;
        String strMyAppId = request.getParameter( PARAMETER_MYAPP_ID );
        String strUserLogin = request.getParameter( PARAMETER_USER_LOGIN );
        String strPassword = request.getParameter( PARAMETER_USER_PASSWORD );
        String strExtraData = request.getParameter( PARAMETER_USER_EXTRA_DATA );
        String strApplicationOrder = request.getParameter( PARAMETER_MYAPPS_ORDER );

        if ( StringUtils.isNotBlank( strMyAppId ) && StringUtils.isNumeric( strMyAppId ) )
        {
            int nMyAppId = Integer.parseInt( strMyAppId );
            MyAppsDatabase myApp = (MyAppsDatabase) MyAppsDatabaseService.getInstance( ).findByPrimaryKey( nMyAppId, plugin );

            // Check mandatory fields
            if ( myApp != null
                    && !( StringUtils.isNotBlank( myApp.getCode( ) ) && StringUtils.isBlank( strUserLogin ) || StringUtils.isNotBlank( myApp.getPassword( ) )
                            && StringUtils.isBlank( strPassword ) )
                    && ( ( StringUtils.isNotBlank( myApp.getData( ) ) && StringUtils.isNotBlank( strExtraData ) ) || ( StringUtils.isBlank( myApp.getData( ) ) || StringUtils
                            .isBlank( myApp.getDataHeading( ) ) ) ) )
            {
                String strUserName = user.getName( );
                myAppsUser = (MyAppsDatabaseUser) MyAppsDatabaseService.getInstance( ).getCredential( nMyAppId, strUserName, plugin );

                if ( myAppsUser == null )
                {
                    myAppsUser = new MyAppsDatabaseUser( );
                }
                myAppsUser.setName( strUserName );
                myAppsUser.setIdApplication( nMyAppId );
                myAppsUser.setStoredUserName( ( strUserLogin != null ) ? strUserLogin : StringUtils.EMPTY );
                myAppsUser.setStoredUserPassword( ( strPassword != null ) ? strPassword : StringUtils.EMPTY );
                myAppsUser.setStoredUserData( ( strExtraData != null ) ? strExtraData : StringUtils.EMPTY );
                myAppsUser.setApplicationOrder( NumberUtils.toInt( strApplicationOrder, NumberUtils.INTEGER_ONE ) );
            }
            else
            {
                SiteMessageService.setMessage( request, Messages.MANDATORY_FIELDS, SiteMessage.TYPE_STOP );
            }
        }
        else
        {
            SiteMessageService.setMessage( request, Messages.MANDATORY_FIELDS, SiteMessage.TYPE_STOP );
        }

        return myAppsUser;
    }
}
