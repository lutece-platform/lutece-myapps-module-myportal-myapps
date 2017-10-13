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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.myapps.business.MyApps;
import fr.paris.lutece.plugins.myapps.modules.database.business.MyAppsDatabaseHome;
import fr.paris.lutece.plugins.myapps.modules.database.business.MyAppsDatabaseUser;
import fr.paris.lutece.plugins.myapps.modules.database.business.MyAppsDatabaseUserHome;
import fr.paris.lutece.plugins.myapps.modules.database.service.MyAppsDatabasePlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;

/**
 * 
 * MyPortalMyAppsService
 *
 */
public class MyPortalMyAppsService
{
    /**
     * Name of the bean of the service
     */
    public static final String BEAN_NAME = "myportal-myapps.myPortalMyAppsService";

    /**
     * Return the list of MyApps of a user ordered with the order specify by the user
     * 
     * @param strUserName
     *            the name of the user
     * @return the list of all MyApps of the user ordered
     */
    public List<MyApps> getOrderedMyAppsList( String strUserName )
    {
        Plugin plugin = PluginService.getPlugin( MyAppsDatabasePlugin.PLUGIN_NAME );
        return MyAppsDatabaseHome.getMyAppsListByUser( strUserName, plugin );
    }

    /**
     * Return the list of all MyApps id ordered by the order specified by the user
     * 
     * @param strUserName
     *            the name of the user
     * @return the list of all MyApps id ordered by the order specified by the user
     */
    public List<Integer> getOrderedMyAppsIdList( String strUserName )
    {
        List<Integer> listMyAppsId = new ArrayList<>( );

        List<MyApps> listMyApps = getOrderedMyAppsList( strUserName );
        if ( listMyApps != null && !listMyApps.isEmpty( ) )
        {
            for ( MyApps myApps : listMyApps )
            {
                listMyAppsId.add( myApps.getIdApplication( ) );
            }
        }

        return listMyAppsId;
    }

    /**
     * Return the list of all MyApps of a user
     * 
     * @param strUserName
     *            the name of a user
     * @return the list of all MyApps of a user
     */
    public List<MyAppsDatabaseUser> getUserMyAppsDatabse( String strUserName )
    {
        Plugin plugin = PluginService.getPlugin( MyAppsDatabasePlugin.PLUGIN_NAME );
        return MyAppsDatabaseUserHome.getUserListApplications( strUserName, plugin );
    }

    /**
     * Return the list of all applications order used by the user
     * 
     * @param strUserName
     *            the name of the user
     * @return the list of all applications order used by the user
     */
    public ReferenceList getUserListOrder( String strUserName )
    {
        ReferenceList referenceListOrder = new ReferenceList( );

        List<MyAppsDatabaseUser> listUserMyApps = getUserMyAppsDatabse( strUserName );
        if ( listUserMyApps != null && !listUserMyApps.isEmpty( ) )
        {
            // Create one ReferenceItem for each application from its order
            for ( MyAppsDatabaseUser myAppsDatabaseUser : listUserMyApps )
            {
                String strApplicationOrder = String.valueOf( myAppsDatabaseUser.getApplicationOrder( ) );

                ReferenceItem referenceItemOrder = new ReferenceItem( );
                referenceItemOrder.setCode( strApplicationOrder );
                referenceItemOrder.setName( strApplicationOrder );

                referenceListOrder.add( referenceItemOrder );
            }
        }

        return referenceListOrder;
    }

    /**
     * Returns the list of all applications position that the user can used when he creates a new favorite application
     * 
     * @param strUserName
     *            the name of the user
     * @return the list of all applications position
     */
    public ReferenceList getUserListOrderForCreation( String strUserName )
    {
        // Retrieve the list of all order already in use
        ReferenceList referenceListOrder = getUserListOrder( strUserName );

        String strNextOrderAvailable = String.valueOf( referenceListOrder.size( ) + 1 );

        // Add another element for the next position in the list
        ReferenceItem referenceItemOrder = new ReferenceItem( );
        referenceItemOrder.setCode( strNextOrderAvailable );
        referenceItemOrder.setName( strNextOrderAvailable );
        referenceListOrder.add( referenceItemOrder );

        return referenceListOrder;
    }

    /**
     * Manage the list of current order for all user MyApps with the new MyApps
     * 
     * @param listUserApplicationOrder
     *            the list of all MyApps id ordered
     * @param myAppsUser
     *            the MyApps to order
     * @return the list of the new MyApps order
     */
    public List<Integer> manageMyAppsNewOrderList( List<Integer> listUserApplicationOrder, MyAppsDatabaseUser myAppsUser )
    {
        List<Integer> listMyAppsOrder = new ArrayList<>( listUserApplicationOrder );
        if ( myAppsUser != null )
        {
            int nMyAppsId = myAppsUser.getIdApplication( );
            if ( listMyAppsOrder.isEmpty( ) )
            {
                // If the list is empty the user has no favorites applications
                // so there is no reordering necessary
                listMyAppsOrder.add( nMyAppsId );
            }
            else
            {
                // If the list already contains the element we must remove it before making reordering
                int nIndexMyApps = listMyAppsOrder.indexOf( nMyAppsId );
                if ( nIndexMyApps != NumberUtils.INTEGER_MINUS_ONE )
                {
                    listMyAppsOrder.remove( nIndexMyApps );
                }

                // Add the application id to the list
                int nMyAppsOrder = myAppsUser.getApplicationOrder( );
                listMyAppsOrder.add( nMyAppsOrder + NumberUtils.INTEGER_MINUS_ONE, nMyAppsId );
            }
        }

        return listMyAppsOrder;
    }

    /**
     * Manage the reordering of user MyApps and make the update in database
     * 
     * @param listNewMyAppsOrder
     *            the new list of MyApps order
     * @param strUserName
     *            the name of the user
     */
    public void manageMyAppsReordering( List<Integer> listNewMyAppsOrder, String strUserName )
    {
        if ( listNewMyAppsOrder != null && !listNewMyAppsOrder.isEmpty( ) && StringUtils.isNotBlank( strUserName ) )
        {
            Plugin plugin = PluginService.getPlugin( MyAppsDatabasePlugin.PLUGIN_NAME );
            for ( int i = 0; i < listNewMyAppsOrder.size( ); i++ )
            {
                MyAppsDatabaseUserHome.updateMyAppsDatabseUserOrder( i + NumberUtils.INTEGER_ONE, listNewMyAppsOrder.get( i ), strUserName, plugin );
            }
        }
    }
}
