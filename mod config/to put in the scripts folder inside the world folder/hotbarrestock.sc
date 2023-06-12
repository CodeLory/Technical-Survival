__on_player_uses_item(player, item_tuple, hand) -> 
	refill(player, hand, item_tuple);

__on_player_right_clicks_block(player, item_tuple, hand, block, face, hitvec) ->
	refill(player, hand, item_tuple);

__on_player_breaks_block(player, block) -> 
	if (item = player ~ 'holds', refill(player, 'mainhand', item));

__on_player_interacts_with_entity(player, entity, hand) -> 
	if (item = query(player,'holds',hand), refill(player, hand, item));

__on_player_releases_item(player, item_tuple, hand) -> 
	refill(player, hand, item_tuple);

__on_player_finishes_using_item(player, item_tuple, hand) -> 
	refill(player, hand, item_tuple);

__on_player_attacks_entity(player, entity) -> 
(
	for(l('mainhand', 'offhand'),
		if (item = query(player, 'holds', _), 
			refill(player, _, item)
		)	
	)
);

refill(player, hand, item_tuple) ->
(
	if (!item_tuple, return());
	slot = if(hand=='offhand',40, player ~ 'selected_slot');
	schedule(0, '__refill_endtick', player, item_tuple, slot)
);

__refill_endtick(player, old_item, slot) ->
(
	if(inventory_get(player,slot), return());
	item_name = old_item:0;
	for(range(35, 0, -1), 
		if ((inv_item = inventory_get(player, _)):0 == item_name, 
			inventory_set(player,slot,inv_item:1, inv_item:0, inv_item:2);
			inventory_set(player, _, null);
			return()		
		)
	);
	for(range(35, 0, -1), sbox_slot = _;
		if ( (shulker_box = inventory_get(player, sbox_slot)):0 ~ 'shulker_box', 	
			sbox_items = shulker_box:2:'BlockEntityTag.Items[]';			
			if (sbox_items,
				if (type(sbox_items)!='list', sbox_items = l(sbox_items));
				long_item_name = 'minecraft:'+item_name;				
				for (sbox_items,
					if (_:'id' == long_item_name, 
						inventory_set(player,slot, _:'Count', item_name, _:'tag');
						delete(shulker_box:2, 'BlockEntityTag.Items['+_i+']');
						inventory_set(player, sbox_slot, shulker_box:1, shulker_box:0, shulker_box:2);
						return()
					)
				)
			)
		)
	)
)
