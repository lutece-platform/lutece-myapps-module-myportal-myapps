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
package fr.paris.lutece.plugins.myportal.modules.myapps.web.rs;

import java.util.Base64;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.myapps.modules.database.business.MyAppsDatabase;
import fr.paris.lutece.plugins.myapps.modules.database.business.MyAppsDatabaseHome;
import fr.paris.lutece.plugins.myapps.modules.database.business.MyAppsDatabaseUser;
import fr.paris.lutece.plugins.myapps.modules.database.business.MyAppsDatabaseUserHome;
import fr.paris.lutece.plugins.myapps.modules.database.service.MyAppsDatabasePlugin;
import fr.paris.lutece.plugins.myapps.modules.database.service.MyAppsDatabaseService;
import fr.paris.lutece.plugins.rest.service.RestConstants;
import fr.paris.lutece.portal.service.image.ImageResource;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * REST service for MyPortalMyApps
 */
@Path( RestConstants.BASE_PATH + MyPortalMyAppsRest.PLUGIN_PATH )
public class MyPortalMyAppsRest
{
    // Constants
    protected static final String USER_GUID = "user_guid";
    protected static final String PLUGIN_PATH = "myapps/";
    protected static final String PATH_APPS = "public/apps/";
    protected static final String PATH_APPS_BY_USERGUID = "private/apps/{" + MyPortalMyAppsRest.USER_GUID + "}";

    // Format constants
    private static final String FORMAT_MYAPPS_STATUS_RESPONSE = "status";
    private static final String FORMAT_MYAPPS_RESPONSE_RESULT = "result";
    private static final String FORMAT_MYAPPS_KEY = "apps";
    private static final String FORMAT_MYAPPS_ID = "id";
    private static final String FORMAT_MYAPPS_NAME = "name";
    private static final String FORMAT_MYAPPS_ICON = "icon";
    private static final String FORMAT_MYAPPS_ORDER = "order";

    // Status constants
    private static final String STATUS_OK = "OK";
    private static final String STATUS_KO = "KO";

    /**
     * Return the list of all MyApps of a user
     * 
     *
     * @param request
     *            httpServletRequest
     * @return the json list corresponding to the list of all user MyApps
     */
    @GET
    @Path( MyPortalMyAppsRest.PATH_APPS )
    public Response getUserMyAppsList( @Context HttpServletRequest request )
    {
        String strStatus = STATUS_OK;
        String strFavoritesList = StringUtils.EMPTY;

        LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );
        // the user must be authenticated
        if ( user != null )
        {

            try
            {
                // Retrieve the list of the applications of the user
                Plugin pluginMyAppsDatabase = PluginService.getPlugin( MyAppsDatabasePlugin.PLUGIN_NAME );
                List<MyAppsDatabaseUser> listMyAppsDatabaseUser = MyAppsDatabaseUserHome.getUserListApplications( user.getName( ), pluginMyAppsDatabase );

                // Format the list of applications
                if ( listMyAppsDatabaseUser != null && !listMyAppsDatabaseUser.isEmpty( ) )
                {
                    strFavoritesList = formatUserMyAppsList( listMyAppsDatabaseUser );
                }
            }
            catch( Exception exception )
            {
                // We set the status at KO if an error occurred during the processing
                strStatus = STATUS_KO;
            }
        }
        else
        {
            strStatus = STATUS_KO;

        }

        // Format the response with the given status and the list of favorites
        String strResponse = formatResponse( strStatus, strFavoritesList );

        // Return the response
        return Response.ok( strResponse, MediaType.APPLICATION_JSON ).build( );
    }

    /**
     * Return the list of all MyApps of a user by user guid the rest service is protected by signed request
     *
     * @param request
     *            httpServletRequest
     * @param strGuid
     *            the user Guid
     * @return the json list corresponding to the list of all user MyApps
     */
    @GET
    @Path( MyPortalMyAppsRest.PATH_APPS_BY_USERGUID )
    public Response getUserMyAppsListByGuid( @Context HttpServletRequest request, @PathParam( MyPortalMyAppsRest.USER_GUID ) String strGuid )
    {
        String strStatus = STATUS_OK;
        String strFavoritesList = StringUtils.EMPTY;

        // the user must be authenticated
        if ( !StringUtils.isEmpty( strGuid ) )
        {

            try
            {
                // Retrieve the list of the applications of the user
                Plugin pluginMyAppsDatabase = PluginService.getPlugin( MyAppsDatabasePlugin.PLUGIN_NAME );
                List<MyAppsDatabaseUser> listMyAppsDatabaseUser = MyAppsDatabaseUserHome.getUserListApplications( strGuid, pluginMyAppsDatabase );

                // Format the list of applications
                if ( listMyAppsDatabaseUser != null && !listMyAppsDatabaseUser.isEmpty( ) )
                {
                    strFavoritesList = formatUserMyAppsList( listMyAppsDatabaseUser );
                }
            }
            catch( Exception exception )
            {
                // We set the status at KO if an error occurred during the processing
                strStatus = STATUS_KO;
            }
        }
        else
        {
            strStatus = STATUS_KO;

        }

        // Format the response with the given status and the list of favorites
        String strResponse = formatResponse( strStatus, strFavoritesList );

        // Return the response
        return Response.ok( strResponse, MediaType.APPLICATION_JSON ).build( );
    }

    /**
     * Return the Json response with the given status
     * 
     * @param strStatus
     *            The status of the treatment "OK" by default "KO" if an error occurred during the processing
     * @param strResponse
     *            The response to send
     * @return the Json response with the given status
     */
    private String formatResponse( String strStatus, String strResponse )
    {
        JSONObject jsonResponse = new JSONObject( );
        jsonResponse.accumulate( FORMAT_MYAPPS_STATUS_RESPONSE, strStatus );
        jsonResponse.accumulate( FORMAT_MYAPPS_RESPONSE_RESULT, strResponse );

        return jsonResponse.toString( );
    }

    /**
     * Return the Json of a list of MyApps object
     * 
     * @param listMyAppsDatabaseUser
     *            the list of the MyApps to format
     * @return the Json of a list of MyApps object
     */
    private String formatUserMyAppsList( List<MyAppsDatabaseUser> listMyAppsDatabaseUser )
    {
        JSONObject jsonResponse = new JSONObject( );
        JSONArray jsonAllMyApps = new JSONArray( );

        for ( MyAppsDatabaseUser myAppsDatabaseUser : listMyAppsDatabaseUser )
        {
            JSONObject jsonMyApps = new JSONObject( );
            add( jsonMyApps, myAppsDatabaseUser );
            jsonAllMyApps.add( jsonMyApps );
        }

        jsonResponse.accumulate( FORMAT_MYAPPS_KEY, jsonAllMyApps );

        return jsonResponse.toString( );
    }

    /**
     * Add the data from a MyApps object to a JsonObject
     * 
     * @param jsonMyApps
     *            the Json to include the data
     * @param myAppsDatabaseUser
     *            the MyAppsDatabaseUser to retrieve the data from
     */
    private void add( JSONObject jsonMyApps, MyAppsDatabaseUser myAppsDatabaseUser )
    {
        if ( jsonMyApps != null && myAppsDatabaseUser != null )
        {
            // Retrieve the application associate to the current MyAppsDatabaseUser
            int nIdApplication = myAppsDatabaseUser.getIdApplication( );
            Plugin pluginMyAppsDatabase = PluginService.getPlugin( MyAppsDatabasePlugin.PLUGIN_NAME );
            MyAppsDatabase myAppsDatabase = (MyAppsDatabase) MyAppsDatabaseService.getInstance( ).findByPrimaryKey( nIdApplication, pluginMyAppsDatabase );

            // Collect the data from the MyAppsDatabase
            if ( myAppsDatabase != null )
            {
                int nIdAppsApplication = myAppsDatabase.getIdApplication( );
                jsonMyApps.accumulate( FORMAT_MYAPPS_ID, nIdApplication );
                jsonMyApps.accumulate( FORMAT_MYAPPS_NAME, myAppsDatabase.getName( ) );
                jsonMyApps.accumulate( FORMAT_MYAPPS_ICON, getApplicationIcon( nIdAppsApplication ) );
            }

            // Collect the data from the MyAppsDatabaseUser
            jsonMyApps.accumulate( FORMAT_MYAPPS_ORDER, myAppsDatabaseUser.getApplicationOrder( ) );
        }
    }

    /**
     * Return the icon of the application as string encoded in base 64
     * 
     * @param nIdApplication
     *            The id of the application
     * @return the string representation of the icon of the application
     */
    private String getApplicationIcon( int nIdApplication )
    {
        String strIconEncoded = StringUtils.EMPTY;

        Plugin pluginMyAppsDatabase = PluginService.getPlugin( MyAppsDatabasePlugin.PLUGIN_NAME );
        ImageResource imageResource = MyAppsDatabaseHome.getImageResource( nIdApplication, pluginMyAppsDatabase );

        if ( imageResource != null && imageResource.getImage( ) != null )
        {
            strIconEncoded = Base64.getEncoder( ).encodeToString( imageResource.getImage( ) );
        }

        return strIconEncoded;
    }
}
