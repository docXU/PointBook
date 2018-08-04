<?php
/**
 * Created by PhpStorm.
 * User: xxw
 * Date: 2017/10/22
 * Time: 22:58
 */

namespace app\api\controller;


use think\Controller;
use app\api\model\Weibo as WeiboModel;
use think\Exception;
use think\Log;
use think\Request;
use think\Db;

class Weibo extends Controller
{
    public function get()
    {
        $requestIns = Request::instance();

        $entryId = $requestIns->param('entryId');

        $weiboModel = new WeiboModel();
        $res = $weiboModel->where('sid', $entryId)
            ->order('create_time', 'desc')
            ->select();
        $realData = [];
        foreach ($res as $weibo) {
            $wid = $weibo['id'];
            //按照图片序号顺序排序
            $pics = Db::table('contentimage')->where('wid', $wid)
                ->order('sequence', 'asc')
                ->select();
            $centipede = "";
            foreach ($pics as $pic) {
                $centipede = $centipede . $pic['imgurl'] . ";";
            }
            if ($centipede !== "")
                $weibo['urls'] = $centipede;
            $realData[] = $weibo;
        }
        Log::info($realData);
        return json($realData, 200);
    }

    public function add()
    {
        $requestIns = Request::instance();

        $uid = $requestIns->param('uid');
        $entryId = $requestIns->param('entryId');
        $content = $requestIns->param('content');
        $headimg = $requestIns->param('headimg');
        $username = $requestIns->param('username');

        Db::startTrans();
        try {
            $weibo = new WeiboModel([
                'sid' => $entryId,
                'uid' => $uid,
                'content' => $content,
                'msglevel' => '初来乍到',
                'headimg' => $headimg,
                'username' => $username
            ]);
            $weibo->save();

            $picCount = intval($requestIns->param('picCount'));
            if ($picCount != null) {
                for ($i = 0; $i < $picCount; $i++) {
                    $data = [
                        'wid' => $weibo->id,
                        'imgurl' => $requestIns->param("pic" . $i),
                        'sequence' => $i
                    ];
                    Db::table('contentimage')->insert($data);
                }
            }
            Db::commit();
            return json($weibo->id, 200);
        } catch (Exception $e) {
            Db::rollback();
            return json(-1, 500);
        }
    }

    public function update()
    {
        $requestIns = Request::instance();

        $wid = $requestIns->param('wid');
        $commentcount = $requestIns->param('commentcount');
        $likecount = $requestIns->param('likecount');
        $lowcount = $requestIns->param('lowcount');
    }

    public function counter()
    {
        $requestIns = Request::instance();

        $wid = $requestIns->param('wid');
        $counter_type = $requestIns->param('counter_type');

        $weiboModel = new WeiboModel();
        $weibo = $weiboModel->where('id', $wid)
            ->select();
        if(is_array($weibo) && count($weibo,COUNT_NORMAL) == 1){
            $updateLikeCounter = $weibo[0][$counter_type] + 1;
            $weiboModel->update(['' . $counter_type => $updateLikeCounter], ['id' => $wid]);

            return json($updateLikeCounter, 200);
        }else{
            return json("error", 500);
        }

    }

}