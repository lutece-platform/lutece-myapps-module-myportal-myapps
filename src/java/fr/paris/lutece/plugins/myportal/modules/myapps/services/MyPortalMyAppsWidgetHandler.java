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
package fr.paris.lutece.plugins.myportal.modules.myapps.services;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.myapps.business.MyApps;
import fr.paris.lutece.plugins.myapps.modules.database.business.MyAppsDatabaseHome;
import fr.paris.lutece.plugins.myapps.modules.database.service.MyAppsDatabasePlugin;
import fr.paris.lutece.plugins.myportal.business.Widget;
import fr.paris.lutece.plugins.myportal.service.handler.WidgetHandler;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 *
 * MyApps Widget Handler
 *
 */
public class MyPortalMyAppsWidgetHandler implements WidgetHandler
{
    // Constants
    private static final String NAME = "module.myportal.myapps.widget.name";
    private static final String DESCRIPTION = "module.myportal.myapps.widget.description";
    private static final boolean IS_CUSTOMIZABLE = true;

    // Templates
    private static final String TEMPLATE_WIDGET_MYAPPS = "skin/plugins/myportal/modules/myapps/widget_myapps.html";

    // Properties
    private static final String PROPERTY_MYAPPS_URL_RETURN = "myportal-myapps.urlReturn.myapps";
    private static final String PROPERTY_MYPORTAL_URL_RETURN = "myportal-myapps.urlReturn.myportal";

    // Marks
    private static final String MARK_USER_APPLICATIONS = "user_list_applications";
    private static final String MARK_USER_TYPE_MESSAGERIE = "typeMessagerie";

    private static final String MARK_ID_WIDGET = "id_widget";
    private static final String MARK_MYAPPS_URL_RETURN = "myapps_url_return";
    private static final String MARK_MYPORTAL_URL_RETURN = "myportal_url_return";
    public static final String BUSINESS_INFO_ONLINE_TYPE_MESSAGERIE = "user.business-info.mdpTypeMessagerie";


    /**
     * {@inheritDoc}
     */
    @Override
    public String getName( )
    {
        return I18nService.getLocalizedString( NAME, Locale.FRENCH );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription( )
    {
        return I18nService.getLocalizedString( DESCRIPTION, Locale.FRENCH );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCustomizable( )
    {
        return IS_CUSTOMIZABLE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String renderWidget( Widget widget, LuteceUser luteceUser, HttpServletRequest request )
    {
        // Retrieve the list of the application of the user
        String strUserName = ( luteceUser != null ) ? luteceUser.getName( ) : StringUtils.EMPTY;
        String typeMessagerie= ( luteceUser != null ) ? luteceUser.getUserInfos( ).get( BUSINESS_INFO_ONLINE_TYPE_MESSAGERIE ) : StringUtils.EMPTY;
        typeMessagerie= ( typeMessagerie != null ) ? typeMessagerie : StringUtils.EMPTY;
        Plugin plugin = PluginService.getPlugin( MyAppsDatabasePlugin.PLUGIN_NAME );
        List<MyApps> listUserMyApps = MyAppsDatabaseHome.getMyAppsListByUser( strUserName, plugin );

        // Generate the model
        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_USER_TYPE_MESSAGERIE, typeMessagerie );
        model.put( MARK_USER_APPLICATIONS, listUserMyApps );
        model.put( MARK_ID_WIDGET, ( widget != null ) ? widget.getIdWidget( ) : NumberUtils.INTEGER_MINUS_ONE );
        model.put( MARK_MYAPPS_URL_RETURN, AppPropertiesService.getProperty( PROPERTY_MYAPPS_URL_RETURN ) );
        model.put( MARK_MYPORTAL_URL_RETURN, AppPropertiesService.getProperty( PROPERTY_MYPORTAL_URL_RETURN ) );

        // Populate the template with the model
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_WIDGET_MYAPPS, request.getLocale( ), model );

        return template.getHtml( );
    }

}
