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
import java.util.Arrays;
import java.util.List;

import fr.paris.lutece.plugins.myapps.modules.database.business.MyAppsDatabaseUser;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * 
 * Test Class for MyPortalMyAppsService
 *
 */
public class MyPortalMyAppsServiceTest extends LuteceTestCase
{
    // Constants
    private static final int APPLICATION_ID_1 = 1;
    private static final int APPLICATION_ID_2 = 2;
    private static final int APPLICATION_ID_3 = 3;
    private static final int APPLICATION_ORDER_1 = 1;
    private static final int APPLICATION_ORDER_2 = 2;
    private static final int APPLICATION_ORDER_3 = 3;
    
    // Messages
    private static final String ERROR_MESSAGE_LIST_NULL = "The list is null";
    private static final String ERROR_MESSAGE_LIST_SIZE = "The list have not the good size (expected %d but was %d)";
    private static final String ERROR_MESSAGE_LIST_ELEMENT = "The list element at the position %d is not the expected one (expected %d but was %d)";
    
    // Variables
    private MyAppsDatabaseUser _myAppsToInsert = new MyAppsDatabaseUser( );

    /**
     * Method which test the good reordering of a list when we insert the first application
     */
    public void testCreateListOrderOnInsertFromEmpty( )
    {
        _myAppsToInsert.setIdApplication( APPLICATION_ID_3 );
        _myAppsToInsert.setApplicationOrder( APPLICATION_ORDER_1 );
        
        List<Integer> listCurrentOrder = new ArrayList<Integer>( );        
        List<Integer> listExpectedResults = Arrays.asList( APPLICATION_ID_3 ); 
        
        compareList( listCurrentOrder, listExpectedResults, _myAppsToInsert );
    }
    
    /**
     * Method which test the good reordering of a list when we insert an application at the first position
     */
    public void testCreateListOrderOnInsertBegin( )
    {
        _myAppsToInsert.setIdApplication( APPLICATION_ID_3 );
        _myAppsToInsert.setApplicationOrder( APPLICATION_ORDER_1 );
        
        List<Integer> listCurrentOrder = Arrays.asList( APPLICATION_ID_2, APPLICATION_ID_1 );        
        List<Integer> listExpectedResults = Arrays.asList( APPLICATION_ID_3, APPLICATION_ID_2, APPLICATION_ID_1 );
        
        compareList( listCurrentOrder, listExpectedResults, _myAppsToInsert );
    }
    
    /**
     * Method which test the good reordering of a list when we insert an application in the middle of the list
     */
    public void testCreateListOrderOnInsertMiddle( )
    {
        _myAppsToInsert.setIdApplication( APPLICATION_ID_3 );
        _myAppsToInsert.setApplicationOrder( APPLICATION_ORDER_2 );
        
        List<Integer> listCurrentOrder = Arrays.asList( APPLICATION_ID_2, APPLICATION_ID_1 );        
        List<Integer> listExpectedResults = Arrays.asList( APPLICATION_ID_2, APPLICATION_ID_3, APPLICATION_ID_1 );
        
        compareList( listCurrentOrder, listExpectedResults, _myAppsToInsert );
    }
    
    /**
     * Method which test the good reordering of a list when we insert an application at the end of the list
     */
    public void testCreateListOrderOnInsertEnd( )
    {
        _myAppsToInsert.setIdApplication( APPLICATION_ID_3 );
        _myAppsToInsert.setApplicationOrder( APPLICATION_ORDER_3 );
        
        List<Integer> listCurrentOrder = Arrays.asList( APPLICATION_ID_2, APPLICATION_ID_1 );        
        List<Integer> listExpectedResults = Arrays.asList( APPLICATION_ID_2, APPLICATION_ID_1, APPLICATION_ID_3 );
        
        compareList( listCurrentOrder, listExpectedResults, _myAppsToInsert );
    }
    
    /**
     * Method which test the good reordering of a list when we modify the order of an application to the 
     * beginning of the list
     */
    public void testCreateListOrderOnModifyBegin( )
    {
        _myAppsToInsert.setIdApplication( APPLICATION_ID_3 );
        _myAppsToInsert.setApplicationOrder( APPLICATION_ORDER_1 );
        
        List<Integer> listCurrentOrder = Arrays.asList( APPLICATION_ID_2, APPLICATION_ID_3, APPLICATION_ID_1 );        
        List<Integer> listExpectedResults = Arrays.asList( APPLICATION_ID_3, APPLICATION_ID_2, APPLICATION_ID_1 );
        
        compareList( listCurrentOrder, listExpectedResults, _myAppsToInsert );     
    }
    
    /**
     * Method which test the good reordering of a list when we modify the order of an application to the 
     * middle of the list
     */
    public void testCreateListOrderOnModifyMiddle( )
    {
        _myAppsToInsert.setIdApplication( APPLICATION_ID_3 );
        _myAppsToInsert.setApplicationOrder( APPLICATION_ORDER_2 );
        
        List<Integer> listCurrentOrder = Arrays.asList( APPLICATION_ID_3, APPLICATION_ID_2, APPLICATION_ID_1 );        
        List<Integer> listExpectedResults = Arrays.asList( APPLICATION_ID_2, APPLICATION_ID_3, APPLICATION_ID_1 );
        
        compareList( listCurrentOrder, listExpectedResults, _myAppsToInsert );    
    }
    
    /**
     * Method which test the good reordering of a list when we modify the order of an application to the 
     * end of the list
     */
    public void testCreateListOrderOnModifyEnd( )
    {
        _myAppsToInsert.setIdApplication( APPLICATION_ID_3 );
        _myAppsToInsert.setApplicationOrder( APPLICATION_ORDER_3 );
        
        List<Integer> listCurrentOrder = Arrays.asList( APPLICATION_ID_2, APPLICATION_ID_3, APPLICATION_ID_1 );        
        List<Integer> listExpectedResults = Arrays.asList( APPLICATION_ID_2, APPLICATION_ID_1, APPLICATION_ID_3 );
        
        compareList( listCurrentOrder, listExpectedResults, _myAppsToInsert );
    }

    /**
     * Make the test to compare the new list result compute from the old list with the expected list
     * from the MyApps to insert
     * 
     * @param oldListOrder the old list order
     * @param listExpectedResults the list of expected result
     * @param myAppsToInsert the MyApps to insert in the list
     */
    private void compareList( List<Integer> oldListOrder, List<Integer> listExpectedResults, MyAppsDatabaseUser myAppsToInsert )
    {
        MyPortalMyAppsService myPortalMyAppsService = new MyPortalMyAppsService( );
        List<Integer> listResultOrder = myPortalMyAppsService.manageMyAppsNewOrderList( oldListOrder, myAppsToInsert );
        
        // Check that the list is not null or empty
        assertNotNull( ERROR_MESSAGE_LIST_NULL, listResultOrder );
        
        Integer nExpectedListSize = listExpectedResults.size( );
        Integer nCurrentListSize = listResultOrder.size( );
        assertEquals( String.format( ERROR_MESSAGE_LIST_SIZE, nExpectedListSize, nCurrentListSize ), nExpectedListSize, nCurrentListSize );
        
        // Check that the result list contains the element at the good position
        for ( int i = 0; i < listExpectedResults.size( ); i++ )
        {
            Integer nExpectedId = listExpectedResults.get( i );
            Integer nCurrentId = listResultOrder.get( i );
            assertEquals( String.format( ERROR_MESSAGE_LIST_ELEMENT, i, nExpectedId, nCurrentId ), nExpectedId, nCurrentId );
        }
    }
}
