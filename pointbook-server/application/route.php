<?php
// +----------------------------------------------------------------------
// | ThinkPHP [ WE CAN DO IT JUST THINK ]
// +----------------------------------------------------------------------
// | Copyright (c) 2006~2016 http://thinkphp.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed ( http://www.apache.org/licenses/LICENSE-2.0 )
// +----------------------------------------------------------------------
// | Author: liu21st <liu21st@gmail.com>
// +----------------------------------------------------------------------

return [
    '__pattern__' => [
        'name' => '\w+',
    ],
    '[hello]' => [
        ':id' => ['index/hello', ['method' => 'get'], ['id' => '\d+']],
        ':name' => ['index/hello', ['method' => 'post']],
    ],
    '[marker]' => [
        'get' => ['api/Marker/get'],
        'add' => ['api/Marker/add']
    ],
    '[weibo]' => [
        'upload' => ['api/Weibo/upload'],
        'get' => ['api/Weibo/get'],
        'add' => ['api/Weibo/add'],
        'counter' => ['api/Weibo/counter']
    ],
    '[tag]' => [
        'add' => ['api/Tag/add'],
        'get' => ['api/Tag/get'],
        'delete' => ['api/Tag/delete']
    ],
    '[user]' => [
        'register' => ['api/User/register'],
        'get' => ['api/User/get'],
        'verify' => ['api/User/verify'],
        'update' => ['api/User/update']
    ]

];
